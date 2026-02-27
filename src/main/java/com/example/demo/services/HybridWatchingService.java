package com.example.demo.services;

import com.example.demo.models.Notification;
import com.example.demo.models.WatchingProgress;
import com.example.demo.repositories.WatchingProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Hybrid Storage Service: Redis (fast) + Database (persistent)
 * Redis cho performance, Database cho reliability  
 */
@Service
public class HybridWatchingService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private WatchingProgressRepository progressRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private ViewTrackingService viewTrackingService;
    
    @Autowired
    private TrendingService trendingService;

    // Keys patterns
    private static final String WATCHING_LIST = "watching_list:";
    private static final String WATCHING_DETAIL = "watching_detail:";
    private static final String LIVE_SESSION = "live_session:";

    // Overloaded methods for better controller integration
    public Map<String, Object> startWatching(Integer userId, Integer movieId, String movieTitle, Integer totalDuration) {
        return startWatching(userId.toString(), movieId.toString(), movieTitle, totalDuration);
    }

    public Map<String, Object> updateProgress(Integer userId, Integer movieId, Integer currentTime, Integer totalDuration) {
        return updateWatchingTime(userId.toString(), movieId.toString(), currentTime, totalDuration);
    }

    // String overload for controller compatibility
    public Map<String, Object> updateProgress(String userId, String movieId, Integer currentTime, Integer totalDuration) {
        return updateWatchingTime(userId, movieId, currentTime, totalDuration);
    }

    public Map<String, Object> getWatchingProgress(Integer userId, Integer movieId) {
        return getResumeTime(userId.toString(), movieId.toString());
    }

    // String overload for controller compatibility
    public Map<String, Object> getWatchingProgress(String userId, String movieId) {
        return getResumeTime(userId, movieId);
    }

    public List<Map<String, Object>> getWatchingList(Integer userId) {
        Map<String, Object> currentData = getCurrentWatching(userId.toString());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> watchingMovies = (List<Map<String, Object>>) currentData.get("watchingMovies");
        return watchingMovies != null ? watchingMovies : new ArrayList<>();
    }

    // String overload for controller compatibility
    public List<Map<String, Object>> getWatchingList(String userId) {
        Map<String, Object> currentData = getCurrentWatching(userId);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> watchingMovies = (List<Map<String, Object>>) currentData.get("watchingMovies");
        return watchingMovies != null ? watchingMovies : new ArrayList<>();
    }

    public boolean markCompleted(Integer userId, Integer movieId) {
        Map<String, Object> result = completeWatching(userId.toString(), movieId.toString());
        return "SUCCESS".equals(result.get("status"));
    }

    // String overload for controller compatibility  
    public boolean markCompleted(String userId, String movieId) {
        Map<String, Object> result = completeWatching(userId, movieId);
        return "SUCCESS".equals(result.get("status"));
    }

    public boolean removeWatching(Integer userId, Integer movieId) {
        try {
            // Remove from Redis
            String redisKey = "watching_detail:" + userId + ":" + movieId;
            redisTemplate.delete(redisKey);
            
            String listKey = "watching_list:" + userId;
            redisTemplate.opsForSet().remove(listKey, movieId.toString());
            
            // Remove from Database
            progressRepository.deleteByUserIdAndMovieId(userId.toString(), movieId.toString());
            
            // Send notification
            notificationService.sendRealTimeNotification(
                userId.toString(),
                Notification.NotificationType.WATCH_PROGRESS,
                "Phim đã được xóa",
                "Đã xóa phim khỏi danh sách đang xem",
                null, // actionUrl
                null, // relatedMovie
                new HashMap<>() // metadata
            );
            
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error removing watching progress: " + e.getMessage());
            return false;
        }
    }

    // String overload for controller compatibility
    public boolean removeWatching(String userId, String movieId) {
        try {
            // Remove from Redis
            String redisKey = "watching_detail:" + userId + ":" + movieId;
            redisTemplate.delete(redisKey);
            
            String listKey = "watching_list:" + userId;
            redisTemplate.opsForSet().remove(listKey, movieId);
            
            // Remove from Database
            progressRepository.deleteByUserIdAndMovieId(userId, movieId);
            
            // Send notification
            notificationService.sendRealTimeNotification(
                userId,
                Notification.NotificationType.WATCH_PROGRESS,
                "Phim đã được xóa",
                "Đã xóa phim khỏi danh sách đang xem",
                null, // actionUrl
                null, // relatedMovie
                new HashMap<>() // metadata
            );
            
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error removing watching progress: " + e.getMessage());
            return false;
        }
    }

    public Map<String, Object> getWatchingStats(Integer userId) {
        return getWatchingStats(userId.toString());
    }

    public boolean testRedisConnection() {
        try {
            String testKey = "test:connection:" + System.currentTimeMillis();
            redisTemplate.opsForValue().set(testKey, "test", 1, TimeUnit.MINUTES);
            Object result = redisTemplate.opsForValue().get(testKey);
            redisTemplate.delete(testKey);
            return result != null;
        } catch (Exception e) {
            System.err.println("❌ Redis connection test failed: " + e.getMessage());
            return false;
        }
    }

    public boolean testDatabaseConnection() {
        try {
            progressRepository.count();
            return true;
        } catch (Exception e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Bắt đầu xem phim - lưu cả Redis và Database
     */
    public Map<String, Object> startWatching(String userId, String movieId, String movieTitle, Integer totalDuration) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // 1. Lưu vào Database (persistent)
            WatchingProgress progress = progressRepository.findByUserIdAndMovieId(userId, movieId)
                .orElse(new WatchingProgress());
            
            progress.setUserId(userId);
            progress.setMovieId(movieId);
            progress.setMovieTitle(movieTitle);
            progress.setTotalDuration(totalDuration);
            progress.setCurrentTime(0);
            progress.setPercentage(0.0);
            progress.setIsCompleted(false);
            progress.setLastWatched(now);
            
            if (progress.getStartedAt() == null) {
                progress.setStartedAt(now);
            }
            
            progressRepository.save(progress);
            
            // 2. Lưu vào Redis (fast access)
            saveToRedis(progress);
            
            result.put("status", "SUCCESS");
            result.put("message", "✅ Bắt đầu xem phim thành công!");
            result.put("progress", mapToResponse(progress));
            
            return result;
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi bắt đầu xem phim: " + e.getMessage());
            return result;
        }
    }

    /**
     * Cập nhật thời gian xem
     */
    public Map<String, Object> updateWatchingTime(String userId, String movieId, Integer currentTime, Integer totalDuration) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. Lấy từ Redis hoặc Database
            WatchingProgress progress = findWatchingProgress(userId, movieId);
            
            if (progress == null) {
                result.put("status", "ERROR");
                result.put("message", "❌ Phim chưa được bắt đầu xem!");
                return result;
            }
            
            // 2. Update progress
            progress.setCurrentTime(currentTime);
            progress.setLastWatched(LocalDateTime.now());
            
            if (totalDuration != null) {
                progress.setTotalDuration(totalDuration);
            }
            
            progress.calculatePercentage();
            
            // 3. Save to both Redis and Database  
            progressRepository.save(progress);
            saveToRedis(progress);
            
            // 4. Track view progress for analytics and auto-increment views
            viewTrackingService.trackView(userId, movieId, currentTime.longValue(), 
                progress.getTotalDuration() != null ? progress.getTotalDuration().longValue() : totalDuration.longValue());
            
            // 5. Track hourly view for trending calculation
            trendingService.trackHourlyView(movieId);
            
            // 6. Send milestone notifications
            sendMilestoneNotification(userId, progress);
            
            result.put("status", "SUCCESS");
            result.put("message", "✅ Cập nhật thời gian xem thành công!");
            result.put("progress", mapToResponse(progress));
            
            return result;
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi cập nhật thời gian xem!");
            return result;
        }
    }

    /**
     * Lấy danh sách phim đang xem
     */
    public Map<String, Object> getCurrentWatching(String userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. Thử lấy từ Redis trước
            List<Map<String, Object>> watchingMovies = getWatchingFromRedis(userId);
            
            // 2. Nếu Redis trống, fallback sang Database
            if (watchingMovies.isEmpty()) {
                watchingMovies = getWatchingFromDatabase(userId);
                
                // 3. Restore lại vào Redis
                if (!watchingMovies.isEmpty()) {
                    restoreWatchingToRedis(userId);
                }
            }
            
            result.put("status", "SUCCESS");
            result.put("message", "✅ Lấy danh sách phim đang xem thành công!");
            result.put("totalMovies", watchingMovies.size());
            result.put("watchingMovies", watchingMovies);
            
            return result;
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi lấy danh sách phim đang xem!");
            return result;
        }
    }

    /**
     * Lấy thời gian để tiếp tục xem phim
     */
    public Map<String, Object> getResumeTime(String userId, String movieId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            WatchingProgress progress = findWatchingProgress(userId, movieId);
            
            if (progress == null) {
                result.put("status", "ERROR");
                result.put("message", "❌ Không tìm thấy lịch sử xem phim!");
                return result;
            }
            
            result.put("status", "SUCCESS");
            result.put("message", "✅ Tìm thấy vị trí tiếp tục xem!");
            result.put("progress", mapToResponse(progress));
            
            return result;
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi lấy thời gian tiếp tục!");
            return result;
        }
    }

    /**
     * Heartbeat để theo dõi user đang xem
     */
    public Map<String, Object> sendHeartbeat(String userId, String movieId, Integer currentTime) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. Update live session trong Redis
            String liveKey = LIVE_SESSION + userId + ":" + movieId;
            Map<String, Object> liveSession = new HashMap<>();
            liveSession.put("userId", userId);
            liveSession.put("movieId", movieId);
            liveSession.put("isActive", true);
            liveSession.put("lastHeartbeat", LocalDateTime.now().toString());
            if (currentTime != null) {
                liveSession.put("currentTime", currentTime);
            }
            
            redisTemplate.opsForValue().set(liveKey, liveSession, 2, TimeUnit.MINUTES);
            
            // 2. Update progress nếu có currentTime
            if (currentTime != null) {
                updateWatchingTime(userId, movieId, currentTime, null);
            }
            
            result.put("status", "SUCCESS");
            result.put("message", "✅ Heartbeat sent successfully!");
            
            return result;
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi gửi heartbeat!");
            return result;
        }
    }

    /**
     * Overloaded heartbeat method for Integer parameters  
     */
    public Map<String, Object> sendHeartbeat(Integer userId, Integer movieId, Integer currentTime) {
        return sendHeartbeat(userId.toString(), movieId.toString(), currentTime);
    }

    /**
     * Hoàn thành xem phim
     */
    public Map<String, Object> completeWatching(String userId, String movieId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            WatchingProgress progress = findWatchingProgress(userId, movieId);
            
            if (progress != null) {
                progress.markCompleted();
                progress.setLastWatched(LocalDateTime.now());
                
                // Save to Database
                progressRepository.save(progress);
                
                // Update Redis
                saveToRedis(progress);
                
                // Remove from watching list
                String listKey = WATCHING_LIST + userId;
                redisTemplate.opsForSet().remove(listKey, movieId);
                
                // Send completion notification
                sendCompletionNotification(userId, progress);
            }
            
            result.put("status", "SUCCESS");
            result.put("message", "✅ Đánh dấu phim đã xem xong!");
            result.put("progress", progress != null ? mapToResponse(progress) : null);
            
            return result;
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi đánh dấu hoàn thành!");
            return result;
        }
    }

    /**
     * Lấy thống kê xem phim
     */
    public Map<String, Object> getWatchingStats(String userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Long currentlyWatching = progressRepository.countCurrentlyWatching(userId);
            Long completedMovies = progressRepository.countCompleted(userId);
            Long totalWatchTime = progressRepository.getTotalWatchTime(userId);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("currentlyWatching", currentlyWatching != null ? currentlyWatching : 0);
            stats.put("completedMovies", completedMovies != null ? completedMovies : 0);
            stats.put("totalWatchTimeSeconds", totalWatchTime != null ? totalWatchTime : 0);
            stats.put("totalWatchTimeHours", totalWatchTime != null ? Math.round(totalWatchTime / 3600.0 * 10) / 10.0 : 0);
            
            result.put("status", "SUCCESS");
            result.put("message", "✅ Lấy thống kê thành công!");
            result.put("stats", stats);
            
            return result;
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi lấy thống kê!");
            return result;
        }
    }

    // ===== HELPER METHODS =====

    /**
     * Lấy WatchingProgress từ Redis hoặc Database
     */
    private WatchingProgress findWatchingProgress(String userId, String movieId) {
        // 1. Thử Redis trước
        String detailKey = WATCHING_DETAIL + userId + ":" + movieId;
        Map<Object, Object> redisData = redisTemplate.opsForHash().entries(detailKey);
        
        if (!redisData.isEmpty()) {
            return mapFromRedis(redisData, userId, movieId);
        }
        
        // 2. Fallback sang Database
        Optional<WatchingProgress> dbProgress = progressRepository.findByUserIdAndMovieId(userId, movieId);
        if (dbProgress.isPresent()) {
            // Restore lại Redis
            saveToRedis(dbProgress.get());
            return dbProgress.get();
        }
        
        return null;
    }

    /**
     * Lưu WatchingProgress vào Redis
     */
    private void saveToRedis(WatchingProgress progress) {
        String listKey = WATCHING_LIST + progress.getUserId();
        String detailKey = WATCHING_DETAIL + progress.getUserId() + ":" + progress.getMovieId();
        
        // Add to watching list
        if (!progress.getIsCompleted()) {
            redisTemplate.opsForSet().add(listKey, progress.getMovieId());
            redisTemplate.expire(listKey, 7, TimeUnit.DAYS);
        }
        
        // Save details
        Map<String, Object> detailData = new HashMap<>();
        detailData.put("movieId", progress.getMovieId());
        detailData.put("movieTitle", progress.getMovieTitle());
        detailData.put("currentTime", progress.getCurrentTime());
        detailData.put("totalDuration", progress.getTotalDuration());
        detailData.put("percentage", progress.getPercentage());
        detailData.put("startedAt", progress.getStartedAt().toString());
        detailData.put("lastWatched", progress.getLastWatched().toString());
        detailData.put("isCompleted", progress.getIsCompleted());
        
        redisTemplate.opsForHash().putAll(detailKey, detailData);
        redisTemplate.expire(detailKey, progress.getIsCompleted() ? 60 : 30, TimeUnit.DAYS);
    }

    /**
     * Lấy danh sách watching từ Redis
     */
    private List<Map<String, Object>> getWatchingFromRedis(String userId) {
        String listKey = WATCHING_LIST + userId;
        Set<Object> movieIds = redisTemplate.opsForSet().members(listKey);
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        if (movieIds != null) {
            for (Object movieId : movieIds) {
                String detailKey = WATCHING_DETAIL + userId + ":" + movieId;
                Map<Object, Object> detail = redisTemplate.opsForHash().entries(detailKey);
                
                if (!detail.isEmpty()) {
                    Map<String, Object> movieData = new HashMap<>();
                    detail.forEach((k, v) -> movieData.put(k.toString(), v));
                    
                    // Check live session
                    String liveKey = LIVE_SESSION + userId + ":" + movieId;
                    boolean isLive = redisTemplate.hasKey(liveKey);
                    movieData.put("isCurrentlyWatching", isLive);
                    
                    result.add(movieData);
                }
            }
        }
        
        return result;
    }

    /**
     * Lấy danh sách watching từ Database
     */
    private List<Map<String, Object>> getWatchingFromDatabase(String userId) {
        List<WatchingProgress> progressList = progressRepository.findByUserIdAndIsCompletedFalse(userId);
        
        return progressList.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Restore watching list từ Database vào Redis
     */
    private void restoreWatchingToRedis(String userId) {
        List<WatchingProgress> progressList = progressRepository.findByUserIdAndIsCompletedFalse(userId);
        
        for (WatchingProgress progress : progressList) {
            saveToRedis(progress);
        }
    }

    /**
     * Map WatchingProgress to response format
     */
    private Map<String, Object> mapToResponse(WatchingProgress progress) {
        Map<String, Object> result = new HashMap<>();
        result.put("movieId", progress.getMovieId());
        result.put("movieTitle", progress.getMovieTitle());
        result.put("currentTime", progress.getCurrentTime());
        result.put("totalDuration", progress.getTotalDuration());
        result.put("percentage", progress.getPercentage());
        result.put("isCompleted", progress.getIsCompleted());
        result.put("startedAt", progress.getStartedAt().toString());
        result.put("lastWatched", progress.getLastWatched().toString());
        result.put("isCurrentlyWatching", false); // Will be updated by caller
        return result;
    }

    /**
     * Map Redis data to WatchingProgress
     */
    private WatchingProgress mapFromRedis(Map<Object, Object> redisData, String userId, String movieId) {
        WatchingProgress progress = new WatchingProgress();
        progress.setUserId(userId);
        progress.setMovieId(movieId);
        progress.setMovieTitle((String) redisData.get("movieTitle"));
        progress.setCurrentTime((Integer) redisData.get("currentTime"));
        progress.setTotalDuration((Integer) redisData.get("totalDuration"));
        progress.setPercentage((Double) redisData.get("percentage"));
        progress.setIsCompleted((Boolean) redisData.get("isCompleted"));
        
        if (redisData.get("startedAt") != null) {
            progress.setStartedAt(LocalDateTime.parse((String) redisData.get("startedAt")));
        }
        if (redisData.get("lastWatched") != null) {
            progress.setLastWatched(LocalDateTime.parse((String) redisData.get("lastWatched")));
        }
        
        return progress;
    }

    /**
     * Send milestone notification
     */
    private void sendMilestoneNotification(String userId, WatchingProgress progress) {
        if (progress.getPercentage() != null && 
            progress.getPercentage() >= 50 && progress.getPercentage() < 55) {
            try {
                notificationService.sendRealTimeNotification(
                    userId,
                    Notification.NotificationType.WATCH_PROGRESS,
                    "📺 Đã xem được nửa phim",
                    "Bạn đã xem được 50% phim '" + progress.getMovieTitle() + "'. Tiếp tục thưởng thức nhé!",
                    null, null, new HashMap<>()
                );
            } catch (Exception e) {
                System.err.println("❌ Failed to send milestone notification: " + e.getMessage());
            }
        }
    }

    /**
     * Send completion notification
     */
    private void sendCompletionNotification(String userId, WatchingProgress progress) {
        try {
            notificationService.sendRealTimeNotification(
                userId,
                Notification.NotificationType.WATCH_PROGRESS,
                "🎉 Hoàn thành phim: " + progress.getMovieTitle(),
                "Bạn đã xem xong phim '" + progress.getMovieTitle() + "'. Tìm phim mới để thưởng thức nhé!",
                null, null, new HashMap<>()
            );
        } catch (Exception e) {
            System.err.println("❌ Failed to send completion notification: " + e.getMessage());
        }
    }
}