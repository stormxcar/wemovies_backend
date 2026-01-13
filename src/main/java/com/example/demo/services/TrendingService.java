package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.example.demo.models.Movie;
import com.example.demo.repositories.MovieRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

@Service
public class TrendingService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private MovieRepository movieRepository;
    
    private static final String TRENDING_SCORE = "trending_score:";
    private static final String HOURLY_VIEWS = "hourly_views:";
    
    /**
     * Calculate trending score based on multiple factors
     */
    public void updateTrendingScore(String movieId) {
        double score = 0.0;
        
        // Factor 1: Recent views (last 24h) - Weight 40%
        Long recentViews = getRecentViews(movieId, 24);
        score += (recentViews * 0.4);
        
        // Factor 2: View velocity (views per hour) - Weight 30% 
        Double viewVelocity = getViewVelocity(movieId);
        score += (viewVelocity * 0.3);
        
        // Factor 3: Social interactions (likes, reviews) - Weight 20%
        Double socialScore = getSocialScore(movieId);
        score += (socialScore * 0.2);
        
        // Factor 4: Completion rate - Weight 10%
        Double completionRate = getCompletionRate(movieId);
        score += (completionRate * 0.1);
        
        // Save trending score to Redis
        String scoreKey = TRENDING_SCORE + movieId;
        redisTemplate.opsForZSet().add("trending_movies", movieId, score);
        redisTemplate.opsForValue().set(scoreKey, score, 1, TimeUnit.HOURS);
    }
    
    /**
     * Get trending movies list
     */
    public List<String> getTrendingMovies(int limit) {
        Set<Object> rawIds = redisTemplate.opsForZSet()
            .reverseRange("trending_movies", 0, limit - 1);
        Set<String> trendingIds = rawIds != null ? 
            rawIds.stream().map(Object::toString).collect(Collectors.toSet()) : 
            Collections.emptySet();
        return new ArrayList<>(trendingIds);
    }
    
    /**
     * Track hourly views for velocity calculation
     */
    public void trackHourlyView(String movieId) {
        String hour = String.valueOf(LocalDateTime.now().getHour());
        String key = HOURLY_VIEWS + movieId + ":" + hour;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, 25, TimeUnit.HOURS); // Keep for 25h
    }
    
    private Long getRecentViews(String movieId, int hours) {
        // Implementation: Count views from Redis or History table
        return 0L; // Placeholder
    }
    
    private Double getViewVelocity(String movieId) {
        // Calculate views per hour trend
        return 0.0; // Placeholder
    }
    
    private Double getSocialScore(String movieId) {
        // Count likes, reviews, shares, etc.
        return 0.0; // Placeholder
    }
    
    private Double getCompletionRate(String movieId) {
        // Percentage of users who completed watching
        return 0.0; // Placeholder
    }
}