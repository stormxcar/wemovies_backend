package com.example.demo.controllers;

import com.example.demo.dto.request.WatchProgressRequest;
// import com.example.demo.enums.WatchStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis-Only "Phim Đang Xem" Controller
 * Không cần database entities, chỉ dùng Redis
 */
@RestController
@RequestMapping("/api/redis-watching")
public class RedisWatchingController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Keys patterns
    private static final String WATCHING_LIST = "watching_list:";
    private static final String WATCHING_DETAIL = "watching_detail:";
    private static final String LIVE_SESSION = "live_session:";

    /**
     * Bắt đầu xem phim
     * POST /api/redis-watching/start
     */
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startWatching(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Extract parameters with logging
            String userId = (String) request.get("userId");
            String movieId = (String) request.get("movieId");
            String movieTitle = (String) request.get("movieTitle");
            Integer totalDuration = request.get("totalDuration") != null ?
                ((Number) request.get("totalDuration")).intValue() : 7200;

            System.out.println("🎬 START WATCHING REQUEST:");
            System.out.println("   - userId: " + userId);
            System.out.println("   - movieId: " + movieId);
            System.out.println("   - movieTitle: " + movieTitle);
            System.out.println("   - totalDuration: " + totalDuration);

            // Validate required parameters
            if (userId == null || userId.trim().isEmpty()) {
                result.put("status", "ERROR");
                result.put("message", "❌ userId is required!");
                return ResponseEntity.badRequest().body(result);
            }

            if (movieId == null || movieId.trim().isEmpty()) {
                result.put("status", "ERROR");
                result.put("message", "❌ movieId is required!");
                return ResponseEntity.badRequest().body(result);
            }

            if (movieTitle == null || movieTitle.trim().isEmpty()) {
                result.put("status", "ERROR");
                result.put("message", "❌ movieTitle is required!");
                return ResponseEntity.badRequest().body(result);
            }

            // Create Redis keys
            String listKey = WATCHING_LIST + userId;
            String detailKey = WATCHING_DETAIL + userId + ":" + movieId;
            String liveKey = LIVE_SESSION + userId + ":" + movieId;

            System.out.println("📝 Redis keys:");
            System.out.println("   - listKey: " + listKey);
            System.out.println("   - detailKey: " + detailKey);
            System.out.println("   - liveKey: " + liveKey);

            // Create watching detail with safe serialization (NO LocalDateTime objects)
            String currentTimeStr = LocalDateTime.now().toString();
            Map<String, Object> watchingDetail = new HashMap<>();
            watchingDetail.put("movieId", movieId);
            watchingDetail.put("movieTitle", movieTitle);
            watchingDetail.put("currentTime", 0);
            watchingDetail.put("totalDuration", totalDuration);
            watchingDetail.put("percentage", 0.0);
            watchingDetail.put("startedAt", currentTimeStr);
            watchingDetail.put("lastWatched", currentTimeStr);

            // Create live session with safe serialization (NO LocalDateTime objects)
            Map<String, Object> liveSession = new HashMap<>();
            liveSession.put("userId", userId);
            liveSession.put("movieId", movieId);
            liveSession.put("isActive", true);
            liveSession.put("lastHeartbeat", currentTimeStr);

            System.out.println("💾 Saving to Redis...");

            // Save to Redis with error handling
            try {
                // Add to watching list (TTL 7 days)
                redisTemplate.opsForSet().add(listKey, movieId);
                redisTemplate.expire(listKey, 7, TimeUnit.DAYS);
                System.out.println("   ✅ List saved");

                // Save details (TTL 30 days)
                redisTemplate.opsForValue().set(detailKey, watchingDetail, 30, TimeUnit.DAYS);
                System.out.println("   ✅ Detail saved");

                // Save live session (TTL 5 minutes)
                redisTemplate.opsForValue().set(liveKey, liveSession, 5, TimeUnit.MINUTES);
                System.out.println("   ✅ Live session saved");

            } catch (Exception redisException) {
                System.err.println("❌ Redis save error: " + redisException.getMessage());
                redisException.printStackTrace();
                throw redisException;
            }

            // Build successful response
            result.put("status", "SUCCESS");
            result.put("message", "✅ Bắt đầu xem phim thành công!");
            result.put("watchingDetail", watchingDetail);
            result.put("timestamp", currentTimeStr);
            result.put("success", true);

            System.out.println("🎉 Watch session created successfully!");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("❌ ERROR in startWatching:");
            System.err.println("   - Message: " + e.getMessage());
            System.err.println("   - Type: " + e.getClass().getSimpleName());
            if (e.getCause() != null) {
                System.err.println("   - Root cause: " + e.getCause().getMessage());
            }
            e.printStackTrace();
            
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi bắt đầu xem phim: " + e.getMessage());
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * Cập nhật thời gian xem hiện tại
     * POST /api/redis-watching/update-time
     */
    @PostMapping("/update-time")
    public ResponseEntity<Map<String, Object>> updateCurrentTime(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();

        try {
            String userId = (String) request.get("userId");
            String movieId = (String) request.get("movieId");
            Integer currentTime = ((Number) request.get("currentTime")).intValue();
            Integer totalDuration = request.get("totalDuration") != null ? 
                ((Number) request.get("totalDuration")).intValue() : null;

            if (userId == null || movieId == null || currentTime == null) {
                result.put("status", "ERROR");
                result.put("message", "❌ userId, movieId, currentTime are required!");
                return ResponseEntity.badRequest().body(result);
            }

            String detailKey = WATCHING_DETAIL + userId + ":" + movieId;
            Map<String, Object> watchingDetail = (Map<String, Object>) redisTemplate.opsForValue().get(detailKey);

            if (watchingDetail == null) {
                result.put("status", "ERROR");
                result.put("message", "❌ Phim chưa được bắt đầu xem!");
                return ResponseEntity.status(404).body(result);
            }

            // Update progress with safe serialization
            watchingDetail.put("currentTime", currentTime);
            watchingDetail.put("lastWatched", LocalDateTime.now().toString());

            if (totalDuration != null) {
                watchingDetail.put("totalDuration", totalDuration);
            }

            // Calculate percentage
            Integer total = (Integer) watchingDetail.get("totalDuration");
            if (total != null && total > 0) {
                double percentage = (double) currentTime / total * 100;
                watchingDetail.put("percentage", Math.round(percentage * 10) / 10.0);
            }

            // Save back to Redis
            redisTemplate.opsForValue().set(detailKey, watchingDetail, 30, TimeUnit.DAYS);

            result.put("status", "SUCCESS");
            result.put("message", "✅ Cập nhật thời gian xem thành công!");
            result.put("currentTime", currentTime);
            result.put("percentage", watchingDetail.get("percentage"));
            result.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi cập nhật thời gian xem!");
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * Lấy danh sách phim đang xem
     * GET /api/redis-watching/current/{userId}
     */
    @GetMapping("/current/{userId}")
    public ResponseEntity<Map<String, Object>> getCurrentWatching(@PathVariable String userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            String listKey = WATCHING_LIST + userId;
            Set<Object> movieIds = redisTemplate.opsForSet().members(listKey);

            List<Map<String, Object>> watchingMovies = new ArrayList<>();

            if (movieIds != null) {
                for (Object movieId : movieIds) {
                    String detailKey = WATCHING_DETAIL + userId + ":" + movieId;
                    Map<String, Object> detail = (Map<String, Object>) redisTemplate.opsForValue().get(detailKey);

                    if (detail != null) {
                        String liveKey = LIVE_SESSION + userId + ":" + movieId;
                        boolean isLiveWatching = redisTemplate.hasKey(liveKey);
                        detail.put("isCurrentlyWatching", isLiveWatching);

                        watchingMovies.add(detail);
                    }
                }
            }

            // Sort by lastWatched (newest first)
            watchingMovies.sort((a, b) -> {
                String timeA = (String) a.get("lastWatched");
                String timeB = (String) b.get("lastWatched");
                return timeB.compareTo(timeA);
            });

            result.put("status", "SUCCESS");
            result.put("message", "✅ Lấy danh sách phim đang xem thành công!");
            result.put("totalMovies", watchingMovies.size());
            result.put("watchingMovies", watchingMovies);
            result.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi lấy danh sách phim đang xem!");
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * Lấy thời gian xem hiện tại để tiếp tục phim
     * GET /api/redis-watching/resume/{userId}/{movieId}
     */
    @GetMapping("/resume/{userId}/{movieId}")
    public ResponseEntity<Map<String, Object>> getResumeTime(
            @PathVariable String userId, 
            @PathVariable String movieId) {
        
        Map<String, Object> result = new HashMap<>();

        try {
            String detailKey = WATCHING_DETAIL + userId + ":" + movieId;
            Map<String, Object> watchingDetail = (Map<String, Object>) redisTemplate.opsForValue().get(detailKey);

            if (watchingDetail == null) {
                result.put("status", "ERROR");
                result.put("message", "❌ Không tìm thấy lịch sử xem phim!");
                return ResponseEntity.status(404).body(result);
            }

            Integer currentTime = (Integer) watchingDetail.get("currentTime");
            Integer totalDuration = (Integer) watchingDetail.get("totalDuration");
            Double percentage = (Double) watchingDetail.get("percentage");

            result.put("status", "SUCCESS");
            result.put("message", "✅ Tìm thấy vị trí tiếp tục xem!");
            result.put("resumeTime", currentTime != null ? currentTime : 0);
            result.put("totalDuration", totalDuration);
            result.put("percentage", percentage != null ? percentage : 0.0);
            result.put("lastWatched", watchingDetail.get("lastWatched"));
            result.put("movieTitle", watchingDetail.get("movieTitle"));
            result.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi lấy thời gian tiếp tục!");
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * Heartbeat để theo dõi user đang xem (gọi mỗi 30 giây từ video player)
     * POST /api/redis-watching/heartbeat
     */
    @PostMapping("/heartbeat")
    public ResponseEntity<Map<String, Object>> sendHeartbeat(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();

        try {
            String userId = (String) request.get("userId");
            String movieId = (String) request.get("movieId");
            Integer currentTime = request.get("currentTime") != null ? 
                ((Number) request.get("currentTime")).intValue() : null;

            if (userId == null || movieId == null) {
                result.put("status", "ERROR");
                result.put("message", "❌ userId và movieId are required!");
                return ResponseEntity.badRequest().body(result);
            }

            // Cập nhật live session với TTL 2 phút
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

            // Cập nhật thời gian nếu có
            if (currentTime != null) {
                String detailKey = WATCHING_DETAIL + userId + ":" + movieId;
                Map<String, Object> watchingDetail = (Map<String, Object>) redisTemplate.opsForValue().get(detailKey);
                
                if (watchingDetail != null) {
                    watchingDetail.put("currentTime", currentTime);
                    watchingDetail.put("lastWatched", LocalDateTime.now().toString());
                    
                    // Calculate percentage
                    Integer total = (Integer) watchingDetail.get("totalDuration");
                    if (total != null && total > 0) {
                        double percentage = (double) currentTime / total * 100;
                        watchingDetail.put("percentage", Math.round(percentage * 10) / 10.0);
                    }
                    
                    redisTemplate.opsForValue().set(detailKey, watchingDetail, 30, TimeUnit.DAYS);
                }
            }

            result.put("status", "SUCCESS");
            result.put("message", "✅ Heartbeat sent successfully!");
            result.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi gửi heartbeat!");
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * Dừng xem phim
     * DELETE /api/redis-watching/stop
     */
    @DeleteMapping("/stop")
    public ResponseEntity<Map<String, Object>> stopWatching(
            @RequestParam String userId,
            @RequestParam String movieId) {

        Map<String, Object> result = new HashMap<>();

        try {
            String listKey = WATCHING_LIST + userId;
            String detailKey = WATCHING_DETAIL + userId + ":" + movieId;
            String liveKey = LIVE_SESSION + userId + ":" + movieId;

            // Xóa khỏi danh sách đang xem
            redisTemplate.opsForSet().remove(listKey, movieId);

            // Xóa chi tiết 
            redisTemplate.delete(detailKey);

            // Xóa live session
            redisTemplate.delete(liveKey);

            result.put("status", "SUCCESS");
            result.put("message", "✅ Dừng xem phim thành công!");
            result.put("movieId", movieId);
            result.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi dừng xem phim!");
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * Đánh dấu phim đã xem xong
     * POST /api/redis-watching/complete
     */
    @PostMapping("/complete")
    public ResponseEntity<Map<String, Object>> completeWatching(
            @RequestParam String userId,
            @RequestParam String movieId) {

        Map<String, Object> result = new HashMap<>();

        try {
            String detailKey = WATCHING_DETAIL + userId + ":" + movieId;

            // Lấy chi tiết hiện tại
            Map<String, Object> watchingDetail = (Map<String, Object>) redisTemplate.opsForValue().get(detailKey);

            if (watchingDetail != null) {
                // Đánh dấu hoàn thành
                watchingDetail.put("percentage", 100.0);
                watchingDetail.put("status", "COMPLETED");
                watchingDetail.put("completedAt", LocalDateTime.now().toString());
                watchingDetail.put("lastWatched", LocalDateTime.now().toString());
                
                // Lưu lại với TTL dài hơn (60 ngày cho phim đã hoàn thành)
                redisTemplate.opsForValue().set(detailKey, watchingDetail, 60, TimeUnit.DAYS);
                
                // Xóa khỏi danh sách đang xem
                String listKey = WATCHING_LIST + userId;
                redisTemplate.opsForSet().remove(listKey, movieId);
                
                // Xóa live session
                String liveKey = LIVE_SESSION + userId + ":" + movieId;
                redisTemplate.delete(liveKey);
            }

            result.put("status", "SUCCESS");
            result.put("message", "✅ Đánh dấu phim đã xem xong!");
            result.put("watchingDetail", watchingDetail);
            result.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi đánh dấu hoàn thành!");
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * Lấy thống kê xem phim
     * GET /api/redis-watching/stats/{userId}
     */
    @GetMapping("/stats/{userId}")
    public ResponseEntity<Map<String, Object>> getWatchingStats(@PathVariable String userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            String listKey = WATCHING_LIST + userId;

            Long currentlyWatching = redisTemplate.opsForSet().size(listKey);

            // Tổng thời gian xem (từ phim đang xem)
            Set<Object> movieIds = redisTemplate.opsForSet().members(listKey);
            int totalWatchTime = 0;
            int completedMovies = 0;

            if (movieIds != null) {
                for (Object movieId : movieIds) {
                    String detailKey = WATCHING_DETAIL + userId + ":" + movieId;
                    Map<String, Object> detail = (Map<String, Object>) redisTemplate.opsForValue().get(detailKey);
                    
                    if (detail != null) {
                        Integer currentTime = (Integer) detail.get("currentTime");
                        String status = (String) detail.get("status");
                        
                        if (currentTime != null) {
                            totalWatchTime += currentTime;
                        }
                        
                        if ("COMPLETED".equals(status)) {
                            completedMovies++;
                        }
                    }
                }
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("currentlyWatching", currentlyWatching != null ? currentlyWatching : 0);
            stats.put("completedMovies", completedMovies);
            stats.put("totalWatchTimeSeconds", totalWatchTime);
            stats.put("totalWatchTimeHours", Math.round(totalWatchTime / 3600.0 * 10) / 10.0);

            result.put("status", "SUCCESS");
            result.put("message", "✅ Lấy thống kê thành công!");
            result.put("stats", stats);
            result.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi lấy thống kê!");
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * Test Redis connection
     * GET /api/redis-watching/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testRedisConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Test Redis connection with simple operations
            String testKey = "test:connection:" + System.currentTimeMillis();
            String testValue = "Redis connection test";
            
            // Test write
            redisTemplate.opsForValue().set(testKey, testValue, 1, TimeUnit.MINUTES);
            
            // Test read
            Object retrieved = redisTemplate.opsForValue().get(testKey);
            
            // Test delete
            redisTemplate.delete(testKey);
            
            result.put("status", "SUCCESS");
            result.put("message", "✅ Redis connection is working!");
            result.put("testResult", "Write/Read/Delete successful");
            result.put("retrievedValue", retrieved);
            result.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Redis connection failed!");
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(500).body(result);
        }
    }
}