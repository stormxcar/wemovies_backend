package com.example.demo.controllers;

import com.example.demo.dto.request.WatchProgressRequest;
import com.example.demo.dto.response.WatchProgressResponse;
import com.example.demo.enums.WatchStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

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
    private static final String WATCHING_LIST = "watching_list:";  // watching_list:userId → Set<movieId>
    private static final String WATCHING_DETAIL = "watching_detail:"; // watching_detail:userId:movieId → JSON
    private static final String LIVE_SESSION = "live_session:"; // live_session:userId:movieId → JSON

    /**
     * Bắt đầu xem phim
     * POST /api/redis-watching/start
     */
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startWatching(
            @RequestBody Map<String, Object> request) {

        Map<String, Object> result = new HashMap<>();

        try {
            // Extract parameters from request body
            String userId = (String) request.get("userId");
            String movieId = (String) request.get("movieId");
            String movieTitle = (String) request.get("movieTitle");
            Integer totalDuration = request.get("totalDuration") != null ?
                ((Number) request.get("totalDuration")).intValue() : 7200;

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

            // Tạo watching detail
            Map<String, Object> watchingDetail = new HashMap<>();
            watchingDetail.put("movieId", movieId);
            watchingDetail.put("movieTitle", movieTitle);
            watchingDetail.put("currentTime", 0);
            watchingDetail.put("totalDuration", totalDuration);
            watchingDetail.put("percentage", 0.0);
            watchingDetail.put("startedAt", LocalDateTime.now().toString());
            watchingDetail.put("lastWatched", LocalDateTime.now().toString());

            // Lưu vào Redis
            String listKey = WATCHING_LIST + userId;
            String detailKey = WATCHING_DETAIL + userId + ":" + movieId;
            String liveKey = LIVE_SESSION + userId + ":" + movieId;

            // Thêm vào danh sách đang xem (TTL 7 ngày)
            redisTemplate.opsForSet().add(listKey, movieId);
            redisTemplate.expire(listKey, 7, TimeUnit.DAYS);

            // Lưu chi tiết (TTL 30 ngày)
            redisTemplate.opsForValue().set(detailKey, watchingDetail, 30, TimeUnit.DAYS);

            // Tạo live session (TTL 5 phút)
            Map<String, Object> liveSession = new HashMap<>();
            liveSession.put("userId", userId);
            liveSession.put("movieId", movieId);
            liveSession.put("isActive", true);
            liveSession.put("lastHeartbeat", LocalDateTime.now());

            redisTemplate.opsForValue().set(liveKey, liveSession, 5, TimeUnit.MINUTES);

            result.put("status", "SUCCESS");
            result.put("message", "✅ Bắt đầu xem phim thành công!");
            result.put("watchingDetail", watchingDetail);
            result.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi bắt đầu xem phim!");
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * Bắt đầu xem phim (Alternative endpoint với JSON body)
     * POST /api/redis-watching/start-json
     */
    @PostMapping("/start-json")
    public ResponseEntity<Map<String, Object>> startWatchingJson(
            @RequestBody Map<String, Object> request) {

        Map<String, Object> result = new HashMap<>();

        try {
            // Extract parameters from JSON body
            String userId = (String) request.get("userId");
            String movieId = (String) request.get("movieId");
            String movieTitle = (String) request.get("movieTitle");
            Integer totalDuration = request.get("totalDuration") != null ?
                ((Number) request.get("totalDuration")).intValue() : 7200;

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

            // Tạo watching detail
            Map<String, Object> watchingDetail = new HashMap<>();
            watchingDetail.put("movieId", movieId);
            watchingDetail.put("movieTitle", movieTitle);
            watchingDetail.put("currentTime", 0);
            watchingDetail.put("totalDuration", totalDuration);
            watchingDetail.put("percentage", 0.0);
            watchingDetail.put("startedAt", LocalDateTime.now().toString());
            watchingDetail.put("lastWatched", LocalDateTime.now().toString());

            // Lưu vào Redis
            String listKey = WATCHING_LIST + userId;
            String detailKey = WATCHING_DETAIL + userId + ":" + movieId;
            String liveKey = LIVE_SESSION + userId + ":" + movieId;

            // Thêm vào danh sách đang xem (TTL 7 ngày)
            redisTemplate.opsForSet().add(listKey, movieId);
            redisTemplate.expire(listKey, 7, TimeUnit.DAYS);

            // Lưu chi tiết (TTL 30 ngày)
            redisTemplate.opsForValue().set(detailKey, watchingDetail, 30, TimeUnit.DAYS);

            // Tạo live session (TTL 5 phút)
            Map<String, Object> liveSession = new HashMap<>();
            liveSession.put("userId", userId);
            liveSession.put("movieId", movieId);
            liveSession.put("isActive", true);
            liveSession.put("lastHeartbeat", LocalDateTime.now());

            redisTemplate.opsForValue().set(liveKey, liveSession, 5, TimeUnit.MINUTES);

            result.put("status", "SUCCESS");
            result.put("message", "✅ Bắt đầu xem phim thành công!");
            result.put("watchingDetail", watchingDetail);
            result.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi bắt đầu xem phim!");
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * Cập nhật tiến độ xem - Sử dụng DTO
     * PUT /api/redis-watching/progress
     */
    @PutMapping("/progress")
    public ResponseEntity<Map<String, Object>> updateProgress(
            @RequestParam String userId,
            @RequestBody WatchProgressRequest request) {

        Map<String, Object> result = new HashMap<>();

        try {
            String detailKey = WATCHING_DETAIL + userId + ":" + request.getMovieId();
            String liveKey = LIVE_SESSION + userId + ":" + request.getMovieId();

            // Lấy chi tiết hiện tại
            Map<String, Object> watchingDetail = (Map<String, Object>) redisTemplate.opsForValue().get(detailKey);

            if (watchingDetail == null) {
                result.put("status", "ERROR");
                result.put("message", "❌ Không tìm thấy phim đang xem!");
                return ResponseEntity.status(404).body(result);
            }

            // Cập nhật tiến độ
            watchingDetail.put("currentTime", request.getCurrentTime());
            watchingDetail.put("lastWatched", LocalDateTime.now());

            // Cập nhật episode info nếu có
            if (request.getEpisodeId() != null) {
                watchingDetail.put("currentEpisodeId", request.getEpisodeId().toString());
            }
            if (request.getEpisodeNumber() != null) {
                watchingDetail.put("episodeNumber", request.getEpisodeNumber());
            }
            if (request.getTotalEpisodes() != null) {
                watchingDetail.put("totalEpisodes", request.getTotalEpisodes());
            }

            // Tính phần trăm
            Long totalDuration = request.getTotalDuration() != null ? 
                request.getTotalDuration() : (Long) watchingDetail.get("totalDuration");
            if (totalDuration != null) {
                double percentage = (double) request.getCurrentTime() / totalDuration * 100;
                watchingDetail.put("percentage", Math.round(percentage * 10) / 10.0);
                watchingDetail.put("totalDuration", totalDuration);
            }

            // Tự động update status
            double currentPercentage = (double) watchingDetail.getOrDefault("percentage", 0.0);
            if (currentPercentage >= 90.0) {
                watchingDetail.put("status", WatchStatus.COMPLETED.name());
            } else {
                watchingDetail.put("status", WatchStatus.WATCHING.name());
            }

            // Lưu lại với TTL mới
            redisTemplate.opsForValue().set(detailKey, watchingDetail, 30, TimeUnit.DAYS);

            // Refresh live session
            Map<String, Object> liveSession = new HashMap<>();
            liveSession.put("userId", userId);
            liveSession.put("movieId", request.getMovieId().toString());
            liveSession.put("isActive", true);
            liveSession.put("lastHeartbeat", LocalDateTime.now());
            liveSession.put("currentTime", request.getCurrentTime());

            redisTemplate.opsForValue().set(liveKey, liveSession, 5, TimeUnit.MINUTES);

            result.put("status", "SUCCESS");
            result.put("message", "✅ Cập nhật tiến độ thành công!");
            result.put("watchingDetail", watchingDetail);
            result.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi cập nhật tiến độ!");
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * Cập nhật thời gian xem hiện tại (gọi từ video player)
     * POST /api/redis-watching/update-time
     */
    @PostMapping("/update-time")
    public ResponseEntity<Map<String, Object>> updateCurrentTime(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();

        try {
            String userId = (String) request.get("userId");
            String movieId = (String) request.get("movieId");
            Integer currentTime = ((Number) request.get("currentTime")).intValue(); // Thời gian hiện tại (giây)
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

            // Cập nhật thời gian hiện tại
            watchingDetail.put("currentTime", currentTime);
            watchingDetail.put("lastWatched", LocalDateTime.now().toString());

            // Cập nhật totalDuration nếu có
            if (totalDuration != null) {
                watchingDetail.put("totalDuration", totalDuration);
            }

            // Tính phần trăm xem
            Integer total = (Integer) watchingDetail.get("totalDuration");
            if (total != null && total > 0) {
                double percentage = (double) currentTime / total * 100;
                watchingDetail.put("percentage", Math.round(percentage * 10) / 10.0);
            }

            // Lưu lại Redis với TTL 30 ngày
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
                    
                    // Tính phần trăm
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
     * Debug endpoint - Test getCurrentWatching without auth
     * GET /api/redis-watching/debug/current/{userId}
     */
    @GetMapping("/debug/current/{userId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Object>> debugGetCurrentWatching(@PathVariable String userId) {
        return getCurrentWatching(userId);
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

            // Debug: Log key being checked
            System.out.println("🔍 Checking Redis key: " + listKey);

            // Lấy danh sách movieId đang xem
            Set<Object> movieIds = redisTemplate.opsForSet().members(listKey);

            System.out.println("📋 Found movieIds in set: " + (movieIds != null ? movieIds.size() : 0) + " items");
            if (movieIds != null) {
                for (Object movieId : movieIds) {
                    System.out.println("   - MovieId: " + movieId);
                }
            }

            List<Map<String, Object>> watchingMovies = new ArrayList<>();

            if (movieIds != null) {
                for (Object movieId : movieIds) {
                    String detailKey = WATCHING_DETAIL + userId + ":" + movieId;
                    System.out.println("🔍 Checking detail key: " + detailKey);

                    Map<String, Object> detail = (Map<String, Object>) redisTemplate.opsForValue().get(detailKey);

                    if (detail != null) {
                        System.out.println("✅ Found detail for movie: " + movieId);
                        // Kiểm tra có live session không
                        String liveKey = LIVE_SESSION + userId + ":" + movieId;
                        boolean isLiveWatching = redisTemplate.hasKey(liveKey);
                        detail.put("isCurrentlyWatching", isLiveWatching);

                        watchingMovies.add(detail);
                    } else {
                        System.out.println("❌ No detail found for movie: " + movieId);
                    }
                }
            }

            // Sắp xếp theo lastWatched mới nhất
            watchingMovies.sort((a, b) -> {
                String timeA = (String) a.get("lastWatched");
                String timeB = (String) b.get("lastWatched");
                return timeB.compareTo(timeA); // String comparison sẽ work với ISO format
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
     * Debug endpoint - Fix missing watching details (No Auth Required)
     * POST /api/redis-watching/debug/fix-details
     */
    @PostMapping("/debug/fix-details")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Object>> fixMissingDetails(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();

        try {
            String userId = (String) request.get("userId");
            if (userId == null || userId.trim().isEmpty()) {
                result.put("status", "ERROR");
                result.put("message", "❌ userId is required!");
                return ResponseEntity.badRequest().body(result);
            }

            String listKey = WATCHING_LIST + userId;
            Set<Object> movieIds = redisTemplate.opsForSet().members(listKey);

            int fixedCount = 0;
            List<String> fixedMovies = new ArrayList<>();

            if (movieIds != null) {
                for (Object movieIdObj : movieIds) {
                    String movieId = movieIdObj.toString();
                    String detailKey = WATCHING_DETAIL + userId + ":" + movieId;

                    // Check if detail exists
                    if (!redisTemplate.hasKey(detailKey)) {
                        // Create missing detail with default values
                        Map<String, Object> watchingDetail = new HashMap<>();
                        watchingDetail.put("movieId", movieId);
                        watchingDetail.put("movieTitle", "Unknown Title (Fixed)");
                        watchingDetail.put("currentTime", 0);
                        watchingDetail.put("totalDuration", 7200);
                        watchingDetail.put("percentage", 0.0);
                        watchingDetail.put("startedAt", LocalDateTime.now().toString());
                        watchingDetail.put("lastWatched", LocalDateTime.now().toString());

                        redisTemplate.opsForValue().set(detailKey, watchingDetail, 30, TimeUnit.DAYS);
                        fixedCount++;
                        fixedMovies.add(movieId);

                        System.out.println("🔧 Fixed missing detail for movie: " + movieId);
                    }
                }
            }

            result.put("status", "SUCCESS");
            result.put("message", "✅ Fixed " + fixedCount + " missing watching details!");
            result.put("fixedMovies", fixedMovies);
            result.put("timestamp", LocalDateTime.now().toString());
            result.put("fixedMovies", fixedMovies);
            result.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi fix missing details!");
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * Debug endpoint - List all watching keys (No Auth Required)
     * GET /api/redis-watching/debug/keys
     */
    @GetMapping("/debug/keys")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Object>> debugKeys() {
        Map<String, Object> result = new HashMap<>();

        try {
            // Scan for all watching_list keys
            Set<String> allKeys = redisTemplate.keys("watching_list:*");
            List<Map<String, Object>> keyDetails = new ArrayList<>();

            if (allKeys != null) {
                for (String key : allKeys) {
                    Map<String, Object> keyInfo = new HashMap<>();
                    keyInfo.put("key", key);

                    // Extract userId from key
                    String userId = key.replace("watching_list:", "");
                    keyInfo.put("userId", userId);

                    // Get members
                    Set<Object> members = redisTemplate.opsForSet().members(key);
                    keyInfo.put("movieCount", members != null ? members.size() : 0);
                    keyInfo.put("movies", members);

                    // Check TTL
                    Long ttl = redisTemplate.getExpire(key);
                    keyInfo.put("ttl", ttl);

                    keyDetails.add(keyInfo);
                }
            }

            result.put("status", "SUCCESS");
            result.put("totalKeys", keyDetails.size());
            result.put("keys", keyDetails);
            result.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi debug keys!");
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
            result.put("timestamp", LocalDateTime.now());

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
                // Đánh dấu completed
                watchingDetail.put("percentage", 100.0);
                watchingDetail.put("completed", true);
                watchingDetail.put("completedAt", LocalDateTime.now());
                watchingDetail.put("currentTime", watchingDetail.get("totalDuration"));

                // Lưu lại
                redisTemplate.opsForValue().set(detailKey, watchingDetail, 30, TimeUnit.DAYS);

                // Xóa khỏi danh sách đang xem
                String listKey = WATCHING_LIST + userId;
                redisTemplate.opsForSet().remove(listKey, movieId);

                // Thêm vào danh sách đã xem (optional)
                String completedKey = "completed_list:" + userId;
                redisTemplate.opsForSet().add(completedKey, movieId);
                redisTemplate.expire(completedKey, 90, TimeUnit.DAYS);
            }

            result.put("status", "SUCCESS");
            result.put("message", "✅ Đánh dấu phim đã xem xong!");
            result.put("watchingDetail", watchingDetail);
            result.put("timestamp", LocalDateTime.now());

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
            String completedKey = "completed_list:" + userId;

            Long currentlyWatching = redisTemplate.opsForSet().size(listKey);
            Long completedMovies = redisTemplate.opsForSet().size(completedKey);

            // Tổng thời gian xem (từ phim đang xem)
            Set<Object> movieIds = redisTemplate.opsForSet().members(listKey);
            int totalWatchTime = 0;

            if (movieIds != null) {
                for (Object movieId : movieIds) {
                    String detailKey = WATCHING_DETAIL + userId + ":" + movieId;
                    Map<String, Object> detail = (Map<String, Object>) redisTemplate.opsForValue().get(detailKey);

                    if (detail != null) {
                        Integer currentTime = (Integer) detail.get("currentTime");
                        if (currentTime != null) {
                            totalWatchTime += currentTime;
                        }
                    }
                }
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("currentlyWatching", currentlyWatching != null ? currentlyWatching : 0);
            stats.put("completedMovies", completedMovies != null ? completedMovies : 0);
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
}