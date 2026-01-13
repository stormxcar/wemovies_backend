package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.example.demo.repositories.MovieRepository;
import com.example.demo.models.Movie;

import java.util.concurrent.TimeUnit;
import java.util.UUID;

@Service
public class ViewTrackingService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private MovieRepository movieRepository;
    
    private static final String VIEW_TRACKER = "view_track:";
    private static final double VIEW_THRESHOLD = 0.3; // 30% watched = 1 view
    
    /**
     * Track viewing progress và auto-increment views
     */
    public void trackView(String userId, String movieId, long currentTime, long totalDuration) {
        double watchPercentage = (double) currentTime / totalDuration;
        String viewKey = VIEW_TRACKER + userId + ":" + movieId;
        
        // Check nếu user đã được tính view cho phim này chưa
        Boolean hasViewed = redisTemplate.hasKey(viewKey);
        
        // Nếu xem >= 30% và chưa tính view thì increment
        if (watchPercentage >= VIEW_THRESHOLD && !hasViewed) {
            // Increment view count trong database
            movieRepository.findById(UUID.fromString(movieId)).ifPresent(movie -> {
                movie.setViews(movie.getViews() + 1);
                movieRepository.save(movie);
            });
            
            // Đánh dấu user đã xem phim này (TTL 7 ngày)
            redisTemplate.opsForValue().set(viewKey, true, 7, TimeUnit.DAYS);
            
            // Log for analytics
            logViewEvent(userId, movieId, watchPercentage);
        }
    }
    
    /**
     * Get real-time view count từ Redis cache
     */
    public long getViewCount(String movieId) {
        String cacheKey = "movie_views:" + movieId;
        Long cachedViews = (Long) redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedViews == null) {
            // Load từ database và cache lại
            var movie = movieRepository.findById(UUID.fromString(movieId));
            long dbViews = movie.map(Movie::getViews).orElse(0L);
            redisTemplate.opsForValue().set(cacheKey, dbViews, 1, TimeUnit.HOURS);
            return dbViews;
        }
        
        return cachedViews;
    }
    
    private void logViewEvent(String userId, String movieId, double percentage) {
        // Log cho analytics system
        System.out.println(String.format(
            "📊 VIEW COUNTED: User %s watched %s (%.1f%%)", 
            userId, movieId, percentage * 100
        ));
    }
}