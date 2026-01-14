package com.example.demo.controllers;

import com.example.demo.dto.request.WatchProgressRequest;
import com.example.demo.dto.response.WatchProgressResponse;
import com.example.demo.enums.WatchStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
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
            watchingDetail.put("startedAt", LocalDateTime.now());
            watchingDetail.put("lastWatched", LocalDateTime.now());

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
            watchingDetail.put("startedAt", LocalDateTime.now());
            watchingDetail.put("lastWatched", LocalDateTime.now());

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
     * Lấy danh sách phim đang xem
     * GET /api/redis-watching/current/{userId}
     */
    @GetMapping("/current/{userId}")
    public ResponseEntity<Map<String, Object>> getCurrentWatching(@PathVariable String userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            String listKey = WATCHING_LIST + userId;

            // Lấy danh sách movieId đang xem
            Set<Object> movieIds = redisTemplate.opsForSet().members(listKey);

            List<Map<String, Object>> watchingMovies = new ArrayList<>();

            if (movieIds != null) {
                for (Object movieId : movieIds) {
                    String detailKey = WATCHING_DETAIL + userId + ":" + movieId;
                    Map<String, Object> detail = (Map<String, Object>) redisTemplate.opsForValue().get(detailKey);

                    if (detail != null) {
                        // Kiểm tra có live session không
                        String liveKey = LIVE_SESSION + userId + ":" + movieId;
                        boolean isLiveWatching = redisTemplate.hasKey(liveKey);
                        detail.put("isCurrentlyWatching", isLiveWatching);

                        watchingMovies.add(detail);
                    }
                }
            }

            // Sắp xếp theo lastWatched mới nhất
            watchingMovies.sort((a, b) -> {
                LocalDateTime timeA = (LocalDateTime) a.get("lastWatched");
                LocalDateTime timeB = (LocalDateTime) b.get("lastWatched");
                return timeB.compareTo(timeA);
            });

            result.put("status", "SUCCESS");
            result.put("message", "✅ Lấy danh sách phim đang xem thành công!");
            result.put("totalMovies", watchingMovies.size());
            result.put("watchingMovies", watchingMovies);
            result.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi lấy danh sách phim đang xem!");
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
            result.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi lấy thống kê!");
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
}