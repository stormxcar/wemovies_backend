package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Enhanced Redis Configuration for Upstash Cloud
 * Supports Upstash Redis with automatic SSL and authentication
 */
@Configuration
public class CloudRedisConfig {

    @Value("${spring.redis.url:}")
    private String upstashUrl;

    @Value("${spring.redis.token:}")
    private String upstashToken;

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Value("${spring.redis.password:}")
    private String redisPassword;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Extract host and password from Upstash URL if available
        String host = redisHost;
        String password = redisPassword;
        int port = redisPort;
        boolean useSSL = false;

        // Parse Upstash URL if provided
        if (upstashUrl != null && !upstashUrl.trim().isEmpty()) {
            // Extract host from https://xxx.upstash.io
            host = upstashUrl.replace("https://", "").replace("http://", "");
            password = upstashToken; // Use token as password
            port = 6379; // Upstash default port
            useSSL = true; // Upstash requires SSL
        }

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        
        // Set password if provided
        if (password != null && !password.trim().isEmpty()) {
            config.setPassword(password);
        }
        
        JedisConnectionFactory factory = new JedisConnectionFactory(config);
        
        // Enable SSL for Upstash
        if (useSSL) {
            factory.setUseSsl(true);
        }
        
        return factory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Use String serializer for keys (readable for debugging)
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Use JSON serializer for values (supports complex objects)
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }
}