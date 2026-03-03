package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.example.demo.models.Movie;
import com.example.demo.repositories.MovieRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private static final DateTimeFormatter HOURLY_KEY_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHH");
    
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
        String hourBucket = LocalDateTime.now().format(HOURLY_KEY_FORMAT);
        String key = HOURLY_VIEWS + movieId + ":" + hourBucket;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, 48, TimeUnit.HOURS);

        // Keep score near real-time when view events arrive
        updateTrendingScore(movieId);
    }

    /**
     * Periodic refresh to keep ranking stable even without continuous traffic
     */
    @Scheduled(fixedRate = 300000) // 5 phút
    public void refreshTrendingScores() {
        List<Movie> movies = movieRepository.findAll();
        for (Movie movie : movies) {
            if (movie.getId() != null) {
                updateTrendingScore(movie.getId().toString());
            }
        }
    }
    
    private Long getRecentViews(String movieId, int hours) {
        long total = 0L;
        for (int i = 0; i < hours; i++) {
            String bucket = LocalDateTime.now().minusHours(i).format(HOURLY_KEY_FORMAT);
            String key = HOURLY_VIEWS + movieId + ":" + bucket;
            Object value = redisTemplate.opsForValue().get(key);
            total += safeLong(value);
        }
        return total;
    }
    
    private Double getViewVelocity(String movieId) {
        long last3h = getRecentViews(movieId, 3);
        long prev3h = 0L;
        for (int i = 3; i < 6; i++) {
            String bucket = LocalDateTime.now().minusHours(i).format(HOURLY_KEY_FORMAT);
            String key = HOURLY_VIEWS + movieId + ":" + bucket;
            Object value = redisTemplate.opsForValue().get(key);
            prev3h += safeLong(value);
        }

        double currentRate = last3h / 3.0;
        double previousRate = prev3h / 3.0;

        if (previousRate <= 0.0) {
            return currentRate;
        }

        return Math.max(0.0, currentRate - previousRate);
    }
    
    private Double getSocialScore(String movieId) {
        // Count likes, reviews, shares, etc.
        return 0.0; // Placeholder
    }
    
    private Double getCompletionRate(String movieId) {
        // Percentage of users who completed watching
        return 0.0; // Placeholder
    }

    private long safeLong(Object value) {
        if (value == null) {
            return 0L;
        }

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        try {
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            return 0L;
        }
    }
}