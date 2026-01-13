package com.example.demo.controllers;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.NotificationResponse;
import com.example.demo.models.Notification;
import com.example.demo.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * WebSocket message handler - Client subscribe to notifications
     */
    @MessageMapping("/notifications.subscribe")
    @SendTo("/topic/notifications")
    public String subscribeToNotifications(@Payload String userId) {
        return "User " + userId + " subscribed to notifications";
    }
    
    /**
     * REST API: Lấy danh sách thông báo
     * GET /api/notifications
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        
        try {
            String userId = getUserId(authentication);
            Page<NotificationResponse> notifications = notificationService.getNotifications(userId, page, size);
            return ResponseEntity.ok(new ApiResponse<>(true, "Notifications retrieved successfully", notifications));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                new ApiResponse<>(false, "Error retrieving notifications: " + e.getMessage(), null)
            );
        }
    }
    
    /**
     * REST API: Lấy số thông báo chưa đọc
     * GET /api/notifications/unread-count
     */
    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUnreadCount(Authentication authentication) {
        try {
            String userId = getUserId(authentication);
            long count = notificationService.getUnreadCount(userId);
            Map<String, Object> response = Map.of("unreadCount", count);
            return ResponseEntity.ok(new ApiResponse<>(true, "Unread count retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                new ApiResponse<>(false, "Error retrieving unread count: " + e.getMessage(), null)
            );
        }
    }
    
    /**
     * REST API: Đánh dấu thông báo đã đọc
     * PUT /api/notifications/{id}/read
     */
    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable String id, Authentication authentication) {
        try {
            String userId = getUserId(authentication);
            notificationService.markAsRead(userId, id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Notification marked as read successfully", "Read status updated"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                new ApiResponse<>(false, "Error marking notification as read: " + e.getMessage(), null)
            );
        }
    }
    
    /**
     * REST API: Đánh dấu tất cả thông báo đã đọc
     * PUT /api/notifications/read-all
     */
    @PutMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(Authentication authentication) {
        try {
            String userId = getUserId(authentication);
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "All notifications marked as read successfully", "All read status updated"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                new ApiResponse<>(false, "Error marking all notifications as read: " + e.getMessage(), null)
            );
        }
    }
    
    /**
     * TEST API: Gửi thông báo test (chỉ cho dev)
     * POST /api/notifications/test
     */
    @PostMapping("/test")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendTestNotification(
            @RequestParam(defaultValue = "NEW_MOVIE") String type,
            @RequestParam(defaultValue = "Test Notification") String title,
            @RequestParam(defaultValue = "This is a test notification") String message,
            Authentication authentication) {
        
        String userId = getUserId(authentication);
        
        try {
            Notification.NotificationType notificationType = Notification.NotificationType.valueOf(type);
            notificationService.sendRealTimeNotification(
                userId, 
                notificationType, 
                title, 
                message, 
                "/test", 
                null, 
                Map.of("isTest", true)
            );
            
            Map<String, Object> responseData = Map.of(
                "userId", userId,
                "type", type,
                "title", title,
                "message", message,
                "timestamp", System.currentTimeMillis()
            );
            
            return ResponseEntity.ok(new ApiResponse<>(true, "✅ Test notification sent successfully!", responseData));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "❌ Invalid notification type: " + type, null)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                new ApiResponse<>(false, "❌ Error: " + e.getMessage(), null)
            );
        }
    }
    
    /**
     * WebSocket Test endpoint
     * POST /api/notifications/ws-test
     */
    @PostMapping("/ws-test")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testWebSocket(@RequestParam String message) {
        try {
            Map<String, Object> responseData = Map.of(
                "message", message,
                "timestamp", System.currentTimeMillis(),
                "status", "WebSocket test initiated"
            );
            return ResponseEntity.ok(new ApiResponse<>(true, "WebSocket test initiated successfully", responseData));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                new ApiResponse<>(false, "WebSocket test failed: " + e.getMessage(), null)
            );
        }
    }
    
    /**
     * Get notifications by user ID - for admin testing
     * GET /api/notifications/user/{userId}
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotificationsByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Page<NotificationResponse> notifications = notificationService.getNotifications(userId, page, size);
            return ResponseEntity.ok(new ApiResponse<>(true, "User notifications retrieved successfully", notifications));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                new ApiResponse<>(false, "Error retrieving user notifications: " + e.getMessage(), null)
            );
        }
    }
    
    /**
     * Get unread count by user ID
     * GET /api/notifications/user/{userId}/unread-count  
     */
    @GetMapping("/user/{userId}/unread-count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUnreadCountByUserId(@PathVariable String userId) {
        try {
            long count = notificationService.getUnreadCount(userId);
            Map<String, Object> response = Map.of("unreadCount", count);
            return ResponseEntity.ok(new ApiResponse<>(true, "User unread count retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                new ApiResponse<>(false, "Error retrieving user unread count: " + e.getMessage(), null)
            );
        }
    }
    
    /**
     * Send notification to specific user - for admin testing
     * POST /api/notifications/send-to-user
     */
    @PostMapping("/send-to-user") 
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendNotificationToUser(
            @RequestParam String userId,
            @RequestParam String type,
            @RequestParam String message,
            @RequestParam(required = false) String actionUrl,
            @RequestParam(required = false) String metadata) {
        
        try {
            Notification.NotificationType notificationType = Notification.NotificationType.valueOf(type);
            
            Map<String, Object> metadataMap = null;
            if (metadata != null && !metadata.trim().isEmpty()) {
                // Simple key=value parsing, you can enhance this
                metadataMap = Map.of("custom", metadata, "sender", "admin");
            }
            
            notificationService.sendRealTimeNotification(
                userId, 
                notificationType, 
                "Admin Notification",
                message, 
                actionUrl, 
                null,
                metadataMap
            );
            
            Map<String, Object> responseData = Map.of(
                "targetUserId", userId,
                "type", type,
                "message", message,
                "timestamp", System.currentTimeMillis()
            );
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Notification sent to user successfully", responseData));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Invalid notification type: " + type, null)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                new ApiResponse<>(false, "Error sending notification: " + e.getMessage(), null)
            );
        }
    }
    
    private String getUserId(Authentication authentication) {
        // Extract user ID from authentication
        // This depends on your JWT/auth implementation
        return authentication.getName(); // Assumes username = userId
    }
}