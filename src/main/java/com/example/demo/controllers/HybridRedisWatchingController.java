package com.example.demo.controllers;

import com.example.demo.services.HybridWatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Hybrid "Phim Đang Xem" Controller
 * Sử dụng Redis (fast) + Database (persistent) thông qua HybridWatchingService
 */
@RestController
@RequestMapping("/api/hybrid-watching")
public class HybridRedisWatchingController {

    @Autowired
    private HybridWatchingService hybridWatchingService;

    /**
     * Bắt đầu xem phim (sử dụng Hybrid Storage)
     * POST /api/hybrid-watching/start
     */
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startWatching(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Extract parameters
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

            // Use hybrid service to start watching
            Map<String, Object> watchingDetail = hybridWatchingService.startWatching(
                userId, 
                movieId, 
                movieTitle, 
                totalDuration
            );

            result.put("status", "SUCCESS");
            result.put("message", "✅ Bắt đầu xem phim thành công!");
            result.put("watchingDetail", watchingDetail);
            result.put("timestamp", LocalDateTime.now().toString());
            result.put("success", true);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi bắt đầu xem phim: " + e.getMessage());
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * Cập nhật thời gian xem hiện tại (sử dụng Hybrid Storage)
     * POST /api/hybrid-watching/update-time
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

            // Use hybrid service to update progress
            Map<String, Object> updateResult = hybridWatchingService.updateProgress(
                userId, 
                movieId, 
                currentTime, 
                totalDuration
            );

            if (updateResult == null) {
                result.put("status", "ERROR");
                result.put("message", "❌ Phim chưa được bắt đầu xem!");
                return ResponseEntity.status(404).body(result);
            }

            result.put("status", "SUCCESS");
            result.put("message", "✅ Cập nhật thời gian xem thành công!");
            result.put("currentTime", currentTime);
            
            // Debug: Log updateResult structure
            System.out.println("🔧 DEBUG updateResult: " + updateResult);
            
            // Fix: Get percentage from progress object, not directly from updateResult
            Map<String, Object> progressData = (Map<String, Object>) updateResult.get("progress");
            System.out.println("🔧 DEBUG progressData: " + progressData);
            
            if (progressData != null) {
                Object percentageObj = progressData.get("percentage");
                System.out.println("🔧 DEBUG percentageObj: " + percentageObj + " (type: " + (percentageObj != null ? percentageObj.getClass() : "null") + ")");
                result.put("percentage", percentageObj);
            } else {
                System.out.println("🔧 DEBUG progressData is NULL!");
                result.put("percentage", null);
            }
            
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
     * Lấy danh sách phim đang xem (sử dụng Hybrid Storage)
     * GET /api/hybrid-watching/watching-list/{userId}
     */
    @GetMapping("/watching-list/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getWatchingList(@PathVariable String userId) {
        try {
            List<Map<String, Object>> watchingList = hybridWatchingService.getWatchingList(userId);
            return ResponseEntity.ok(watchingList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }

    /**
     * Lấy thời gian xem hiện tại để tiếp tục phim (sử dụng Hybrid Storage)
     * GET /api/hybrid-watching/resume/{userId}/{movieId}
     */
    @GetMapping("/resume/{userId}/{movieId}")
    public ResponseEntity<Map<String, Object>> getResumeTime(
            @PathVariable String userId, 
            @PathVariable String movieId) {
        
        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Object> watchingProgress = hybridWatchingService.getWatchingProgress(userId, movieId);

            if (watchingProgress == null) {
                result.put("status", "ERROR");
                result.put("message", "❌ Không tìm thấy lịch sử xem phim!");
                return ResponseEntity.status(404).body(result);
            }

            result.put("status", "SUCCESS");
            result.put("message", "✅ Tìm thấy vị trí tiếp tục xem!");
            result.put("resumeTime", watchingProgress.get("currentTime"));
            result.put("totalDuration", watchingProgress.get("totalDuration"));
            result.put("percentage", watchingProgress.get("percentage"));
            result.put("lastWatched", watchingProgress.get("lastWatched"));
            result.put("movieTitle", watchingProgress.get("movieTitle"));
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
     * Đánh dấu phim là đã hoàn thành (sử dụng Hybrid Storage)
     * POST /api/hybrid-watching/complete
     */
    @PostMapping("/complete")
    public ResponseEntity<Map<String, Object>> markCompleted(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();

        try {
            String userId = (String) request.get("userId");
            String movieId = (String) request.get("movieId");

            if (userId == null || movieId == null) {
                result.put("status", "ERROR");
                result.put("message", "❌ userId và movieId are required!");
                return ResponseEntity.badRequest().body(result);
            }

            boolean success = hybridWatchingService.markCompleted(
                userId, 
                movieId
            );

            if (success) {
                result.put("status", "SUCCESS");
                result.put("message", "✅ Đánh dấu hoàn thành phim thành công!");
            } else {
                result.put("status", "ERROR");
                result.put("message", "❌ Không tìm thấy phim đang xem!");
            }

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
     * Xóa phim khỏi danh sách đang xem (sử dụng Hybrid Storage)
     * DELETE /api/hybrid-watching/remove
     */
    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeWatching(
            @RequestParam String userId,
            @RequestParam String movieId) {

        Map<String, Object> result = new HashMap<>();

        try {
            boolean success = hybridWatchingService.removeWatching(userId, movieId);

            if (success) {
                result.put("status", "SUCCESS");
                result.put("message", "✅ Xóa phim khỏi danh sách thành công!");
            } else {
                result.put("status", "ERROR");
                result.put("message", "❌ Không tìm thấy phim đang xem!");
            }

            result.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi xóa phim!");
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * Lấy thống kê xem phim (sử dụng Hybrid Storage)
     * GET /api/hybrid-watching/stats/{userId}
     */
    @GetMapping("/stats/{userId}")
    public ResponseEntity<Map<String, Object>> getWatchingStats(@PathVariable String userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Object> stats = hybridWatchingService.getWatchingStats(userId);

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
     * Gửi heartbeat để theo dõi user đang xem (gọi mỗi 30 giây từ video player)
     * POST /api/hybrid-watching/heartbeat
     */
    @PostMapping("/heartbeat")
    public ResponseEntity<Map<String, Object>> sendHeartbeat(@RequestBody Map<String, Object> request) {
        try {
            String userId = (String) request.get("userId");
            String movieId = (String) request.get("movieId");
            Integer currentTime = request.get("currentTime") != null ? 
                ((Number) request.get("currentTime")).intValue() : null;

            if (userId == null || movieId == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("status", "ERROR");
                result.put("message", "❌ userId và movieId are required!");
                return ResponseEntity.badRequest().body(result);
            }

            // Use hybrid service for heartbeat
            Map<String, Object> result = hybridWatchingService.sendHeartbeat(
                userId, movieId, currentTime);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "ERROR");
            result.put("message", "❌ Lỗi khi gửi heartbeat!");
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * Test Hybrid Storage (Redis + Database)
     * GET /api/hybrid-watching/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testHybridStorage() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean redisWorking = hybridWatchingService.testRedisConnection();
            boolean dbWorking = hybridWatchingService.testDatabaseConnection();
            
            result.put("status", "SUCCESS");
            result.put("message", "✅ Hybrid storage test completed!");
            result.put("redisWorking", redisWorking);
            result.put("databaseWorking", dbWorking);
            result.put("hybridMode", redisWorking ? "Redis + Database" : "Database Only");
            result.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Hybrid storage test failed!");
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(500).body(result);
        }
    }
}