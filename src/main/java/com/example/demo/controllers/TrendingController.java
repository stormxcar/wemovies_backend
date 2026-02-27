package com.example.demo.controllers;

import com.example.demo.services.TrendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Trending Controller - Quản lý phim trending và hot movies
 * API endpoints cho trending movies và trending score management
 */
@RestController
@RequestMapping("/api/trending")
public class TrendingController {
    
    @Autowired
    private TrendingService trendingService;
    
    /**
     * Get list of trending movies
     * GET /api/trending/movies?limit=10
     */
    @GetMapping("/movies")
    public ResponseEntity<Map<String, Object>> getTrendingMovies(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "false") boolean includeDetails) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (limit <= 0 || limit > 100) {
                result.put("status", "ERROR");
                result.put("message", "❌ Limit must be between 1 and 100!");
                return ResponseEntity.badRequest().body(result);
            }
            
            List<String> trendingMovieIds = trendingService.getTrendingMovies(limit);
            
            result.put("status", "SUCCESS");
            result.put("trendingMovies", trendingMovieIds);
            result.put("count", trendingMovieIds.size());
            result.put("limit", limit);
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Failed to get trending movies: " + e.getMessage());
            result.put("timestamp", System.currentTimeMillis());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Manual update trending score (Admin only)
     * POST /api/trending/update/{movieId}
     */
    @PostMapping("/update/{movieId}")
    public ResponseEntity<Map<String, Object>> updateTrendingScore(@PathVariable String movieId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (movieId == null || movieId.trim().isEmpty()) {
                result.put("status", "ERROR");
                result.put("message", "❌ movieId is required!");
                return ResponseEntity.badRequest().body(result);
            }
            
            // Update trending score
            trendingService.updateTrendingScore(movieId);
            
            result.put("status", "SUCCESS");
            result.put("message", "✅ Trending score updated successfully!");
            result.put("movieId", movieId);
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Failed to update trending score: " + e.getMessage());
            result.put("timestamp", System.currentTimeMillis());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Track hourly view for trending calculation
     * POST /api/trending/track-view
     */
    @PostMapping("/track-view")
    public ResponseEntity<Map<String, Object>> trackHourlyView(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String movieId = (String) request.get("movieId");
            String userId = (String) request.get("userId");
            
            if (movieId == null || movieId.trim().isEmpty()) {
                result.put("status", "ERROR");
                result.put("message", "❌ movieId is required!");
                return ResponseEntity.badRequest().body(result);
            }
            
            // Track hourly view
            trendingService.trackHourlyView(movieId);
            
            result.put("status", "SUCCESS");
            result.put("message", "✅ Hourly view tracked successfully!");
            result.put("movieId", movieId);
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Failed to track hourly view: " + e.getMessage());
            result.put("timestamp", System.currentTimeMillis());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Batch update trending scores for multiple movies
     * POST /api/trending/batch-update
     */
    @PostMapping("/batch-update")
    public ResponseEntity<Map<String, Object>> batchUpdateTrendingScores(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            @SuppressWarnings("unchecked")
            List<String> movieIds = (List<String>) request.get("movieIds");
            
            if (movieIds == null || movieIds.isEmpty()) {
                result.put("status", "ERROR");
                result.put("message", "❌ movieIds array is required!");
                return ResponseEntity.badRequest().body(result);
            }
            
            // Update trending scores for all movies
            int updatedCount = 0;
            for (String movieId : movieIds) {
                if (movieId != null && !movieId.trim().isEmpty()) {
                    try {
                        trendingService.updateTrendingScore(movieId);
                        updatedCount++;
                    } catch (Exception e) {
                        // Log error but continue with other movies
                        System.err.println("Failed to update trending score for movie " + movieId + ": " + e.getMessage());
                    }
                }
            }
            
            result.put("status", "SUCCESS");
            result.put("message", "✅ Batch trending score update completed!");
            result.put("requestedCount", movieIds.size());
            result.put("updatedCount", updatedCount);
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Failed to batch update trending scores: " + e.getMessage());
            result.put("timestamp", System.currentTimeMillis());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get trending statistics and metrics
     * GET /api/trending/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getTrendingStats() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Get top trending movies with basic stats
            List<String> top10Trending = trendingService.getTrendingMovies(10);
            
            result.put("status", "SUCCESS");
            result.put("totalTrendingMovies", top10Trending.size());
            result.put("top10Movies", top10Trending);
            result.put("lastUpdated", System.currentTimeMillis());
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "❌ Failed to get trending stats: " + e.getMessage());
            result.put("timestamp", System.currentTimeMillis());
        }
        
        return ResponseEntity.ok(result);
    }
}