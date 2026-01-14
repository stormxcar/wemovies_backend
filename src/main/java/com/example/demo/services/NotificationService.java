package com.example.demo.services;

import com.example.demo.dto.response.NotificationResponse;
import com.example.demo.models.Movie;
import com.example.demo.models.Notification;
import com.example.demo.models.auth.User;
import com.example.demo.repositories.NotificationRepository;
import com.example.demo.repositories.auth.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Gửi thông báo realtime cho user
     */
    @Async
    public void sendRealTimeNotification(String userId, Notification.NotificationType type, 
                                       String title, String message, String actionUrl, 
                                       Movie relatedMovie, Map<String, Object> metadata) {
        try {
            User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
            
            // Tạo notification entity
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setType(type);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setActionUrl(actionUrl);
            notification.setRelatedMovie(relatedMovie);
            
            if (metadata != null) {
                notification.setMetadata(objectMapper.writeValueAsString(metadata));
            }
            
            // Lưu vào database
            notification = notificationRepository.save(notification);
            
            // Tạo response DTO
            NotificationResponse response = NotificationResponse.fromEntity(notification);
            response.setTimeAgo(calculateTimeAgo(notification.getSentAt()));
            
            // Gửi realtime qua WebSocket
            messagingTemplate.convertAndSendToUser(
                userId, 
                "/queue/notifications", 
                response
            );
            
            // Gửi update unread count
            long unreadCount = getUnreadCount(userId);
            messagingTemplate.convertAndSendToUser(
                userId, 
                "/queue/unread-count", 
                Map.of("count", unreadCount)
            );
            
            System.out.println("✅ Sent realtime notification to user " + userId + ": " + title);
            
        } catch (Exception e) {
            System.err.println("❌ Error sending notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Gửi thông báo cho multiple users
     */
    @Async
    public void sendBroadcastNotification(List<String> userIds, Notification.NotificationType type,
                                        String title, String message, String actionUrl) {
        for (String userId : userIds) {
            sendRealTimeNotification(userId, type, title, message, actionUrl, null, null);
        }
    }
    
    /**
     * Gửi thông báo broadcast cho tất cả users
     */
    @Async
    public void sendBroadcastToAllUsers(Notification.NotificationType type, String title, 
                                      String message, String actionUrl, Movie relatedMovie, 
                                      Map<String, Object> metadata) {
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            sendRealTimeNotification(user.getId().toString(), type, title, message, 
                                   actionUrl, relatedMovie, metadata);
        }
    }
    
    /**
     * Lấy danh sách thông báo của user
     */
    public Page<NotificationResponse> getNotifications(String userId, int page, int size) {
        User user = userRepository.findById(UUID.fromString(userId))
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationRepository.findByUserOrderBySentAtDesc(user, pageable);
        
        return notifications.map(notification -> {
            NotificationResponse response = NotificationResponse.fromEntity(notification);
            response.setTimeAgo(calculateTimeAgo(notification.getSentAt()));
            return response;
        });
    }
    
    /**
     * Lấy số thông báo chưa đọc
     */
    public long getUnreadCount(String userId) {
        User user = userRepository.findById(UUID.fromString(userId))
            .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.countUnreadByUser(user);
    }
    
    /**
     * Đánh dấu thông báo đã đọc
     */
    public void markAsRead(String userId, String notificationId) {
        Notification notification = notificationRepository.findById(UUID.fromString(notificationId))
            .orElseThrow(() -> new RuntimeException("Notification not found"));
            
        if (!notification.getUser().getId().toString().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        if (!notification.isRead()) {
            notification.markAsRead();
            notificationRepository.save(notification);
            
            // Update unread count realtime
            long unreadCount = getUnreadCount(userId);
            messagingTemplate.convertAndSendToUser(
                userId, 
                "/queue/unread-count", 
                Map.of("count", unreadCount)
            );
        }
    }
    
    /**
     * Đánh dấu tất cả thông báo đã đọc
     */
    public void markAllAsRead(String userId) {
        User user = userRepository.findById(UUID.fromString(userId))
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        notificationRepository.markAllAsReadByUser(user, LocalDateTime.now());
        
        // Update unread count realtime
        messagingTemplate.convertAndSendToUser(
            userId, 
            "/queue/unread-count", 
            Map.of("count", 0)
        );
    }
    
    /**
     * Xóa thông báo cũ (chạy scheduled)
     */
    public void cleanupOldNotifications() {
        LocalDateTime cutoffDate = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        notificationRepository.deleteOldNotifications(cutoffDate);
    }
    
    /**
     * Helper method: Tính thời gian tương đối
     */
    private String calculateTimeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        
        if (minutes < 1) return "Vừa xong";
        if (minutes < 60) return minutes + " phút trước";
        
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        if (hours < 24) return hours + " giờ trước";
        
        long days = ChronoUnit.DAYS.between(dateTime, now);
        if (days < 7) return days + " ngày trước";
        
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    
    /**
     * Quick notification methods for common scenarios
     */
    public void notifyNewEpisode(String userId, Movie movie, String episodeTitle) {
        sendRealTimeNotification(
            userId, 
            Notification.NotificationType.NEW_EPISODE,
            "Tập mới đã ra!",
            "Tập mới của \"" + movie.getTitle() + "\" đã có: " + episodeTitle,
            "/watch/" + movie.getSlug(),
            movie,
            Map.of("episodeTitle", episodeTitle)
        );
    }
    
    public void notifyNewMovie(String userId, Movie movie) {
        sendRealTimeNotification(
            userId,
            Notification.NotificationType.NEW_MOVIE,
            "Phim mới!",
            "\"" + movie.getTitle() + "\" vừa được thêm vào thư viện",
            "/movie/" + movie.getSlug(),
            movie,
            null
        );
    }
    
    public void notifyContinueWatching(String userId, Movie movie, int progress) {
        sendRealTimeNotification(
            userId,
            Notification.NotificationType.CONTINUE_WATCHING,
            "Tiếp tục xem",
            "Bạn đã xem " + progress + "% của \"" + movie.getTitle() + "\"",
            "/watch/" + movie.getSlug(),
            movie,
            Map.of("progress", progress)
        );
    }
}