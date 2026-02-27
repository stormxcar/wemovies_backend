package com.example.demo.controllers;

import com.example.demo.services.ViewTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ViewTracking Controller - Đếm lượt xem tự động và real-time
 * API endpoints cho tracking view và lấy view count
 */
@RestController
@RequestMapping("/api/view-tracking")
public class ViewTrackingController {
    
    @Autowired
    private ViewTrackingService viewTrackingService;
    
    /**
     * Track viewing progress (được call từ video player)
     * POST /api/view-tracking/track
     */
    @PostMapping("/track")
    public ResponseEntity<Map<String, Object>> trackView(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Extract parameters
            String userId = (String) request.get("userId");
            String movieId = (String) request.get("movieId");
            Object currentTimeObj = request.get("currentTime");
            Object totalDurationObj = request.get("totalDuration");
            
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
            
            if (currentTimeObj == null || totalDurationObj == null) {
                result.put("status", "ERROR");
                result.put("message", "❌ currentTime and totalDuration are required!");
                return ResponseEntity.badRequest().body(result);
            }
            
            // Convert to long values
            long currentTime = ((Number) currentTimeObj).longValue();
            long totalDuration = ((Number) totalDurationObj).longValue();
            
            if (totalDuration <= 0) {
                result.put("status", "ERROR");
                result.put("message", "❌ totalDuration must be greater than 0!");
                return ResponseEntity.badRequest().body(result);
            }
            
            // Track the view
            viewTrackingService.trackView(userId, movieId, currentTime, totalDuration);
            
            // Calculate watch percentage for response
            double watchPercentage = (double) currentTime / totalDuration;
            
            result.put("status", "SUCCESS");
            result.put("message", "✅ View tracked successfully!");
            result.put("userId", userId);
            result.put("movieId", movieId);
            result.put("watchPercentage", Math.round(watchPercentage * 100 * 100.0) / 100.0); // Round to 2 decimal places
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Failed to track view: " + e.getMessage());
            result.put("timestamp", System.currentTimeMillis());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get current view count for a movie
     * GET /api/view-tracking/count/{movieId}
     */
    @GetMapping("/count/{movieId}")
    public ResponseEntity<Map<String, Object>> getViewCount(@PathVariable String movieId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (movieId == null || movieId.trim().isEmpty()) {
                result.put("status", "ERROR");
                result.put("message", "❌ movieId is required!");
                return ResponseEntity.badRequest().body(result);
            }
            
            // Get view count
            long viewCount = viewTrackingService.getViewCount(movieId);
            
            result.put("status", "SUCCESS");
            result.put("movieId", movieId);
            result.put("viewCount", viewCount);
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Failed to get view count: " + e.getMessage());
            result.put("timestamp", System.currentTimeMillis());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Batch get view counts for multiple movies
     * POST /api/view-tracking/batch-count
     */
    @PostMapping("/batch-count")
    public ResponseEntity<Map<String, Object>> getBatchViewCount(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            @SuppressWarnings("unchecked")
            java.util.List<String> movieIds = (java.util.List<String>) request.get("movieIds");
            
            if (movieIds == null || movieIds.isEmpty()) {
                result.put("status", "ERROR");
                result.put("message", "❌ movieIds array is required!");
                return ResponseEntity.badRequest().body(result);
            }
            
            Map<String, Long> viewCounts = new HashMap<>();
            for (String movieId : movieIds) {
                if (movieId != null && !movieId.trim().isEmpty()) {
                    long count = viewTrackingService.getViewCount(movieId);
                    viewCounts.put(movieId, count);
                }
            }
            
            result.put("status", "SUCCESS");
            result.put("viewCounts", viewCounts);
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Failed to get batch view counts: " + e.getMessage());
            result.put("timestamp", System.currentTimeMillis());
        }
        
        return ResponseEntity.ok(result);
    }
}