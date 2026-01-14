package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket Configuration for Real-time Notifications
 * Cấu hình WebSocket để gửi thông báo realtime
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable in-memory broker với prefix "/topic" và "/queue"
        config.enableSimpleBroker("/topic", "/queue");
        
        // Prefix cho client gửi message đến server
        config.setApplicationDestinationPrefixes("/app");
        
        // Prefix cho personal messages (user-specific)
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket endpoint cho client kết nối
        registry.addEndpoint("/ws-notifications")
                .setAllowedOriginPatterns(
                    "https://wemovies-frontend.vercel.app",
                    "http://localhost:3000", 
                    "https://localhost:3000",
                    "https://wemovies-backend.onrender.com"
                ) // Allow specific origins for production
                .withSockJS(); // Fallback support for older browsers
    }
}