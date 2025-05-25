package com.example.demo.services;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class MovieHistoryService {

    private final RedisTemplate<String, Object> redisTemplate;

    public MovieHistoryService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveWatchHistory(String userId, String movieId, long timestamp) {
        String key = "user:" + userId + ":movie:" + movieId;
        redisTemplate.opsForValue().set(key, timestamp);
    }

    public Long getWatchHistory(String userId, String movieId) {
        String key = "user:" + userId + ":movie:" + movieId;
        return (Long) redisTemplate.opsForValue().get(key);
    }
}
