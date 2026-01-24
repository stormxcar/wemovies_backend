package com.example.demo.models;

import com.example.demo.models.auth.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "notifications")
public class Notification extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = true)
    private Movie relatedMovie;
    
    private boolean isRead = false;
    
    private String actionUrl;
    
    @Column(name = "sent_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sentAt;
    
    @Column(name = "read_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") 
    private LocalDateTime readAt;
    
    // Thông tin thêm dưới dạng JSON string
    @Column(columnDefinition = "TEXT")
    private String metadata;
    
    @PrePersist
    protected void onCreate() {
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
    }
    
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
    
    public enum NotificationType {
        // Movie related - Enhanced with user requests
        NEW_EPISODE("📺", "Tập mới của series đang follow"),
        NEW_MOVIE("🎬", "Phim mới theo thể loại yêu thích"), 
        MOVIE_REMINDER("⏰", "Nhắc nhở xem tiếp phim dở dang"),
        CONTINUE_WATCHING("▶️", "Tiếp tục xem"),
        WEEKLY_DIGEST("📊", "Weekly digest: phim hot tuần này"),
        WATCH_PROGRESS("📈", "Tiến trình xem phim"),
        WATCHLIST_REMINDER("📝", "Nhắc nhở danh sách xem sau"),
        
        // User activity - Enhanced  
        FRIEND_ACTIVITY("👥", "Bạn bè review phim mới"),
        FRIEND_REVIEW("📝", "Bạn bè đánh giá phim mới"),
        RECOMMENDATION("🎯", "Gợi ý cho bạn"),
        
        // System - Enhanced with maintenance
        SYSTEM("🔔", "Thông báo hệ thống"),
        MAINTENANCE("🔧", "Maintenance/downtime thông báo"),
        UPDATE("🆕", "Cập nhật"),
        
        // Social 
        LIKE_RECEIVED("👍", "Có người thích"),
        COMMENT_RECEIVED("💬", "Có bình luận mới"),
        REVIEW_REPLY("📝", "Phản hồi đánh giá"),
        
        // Promotional
        PROMOTION("🎁", "Khuyến mãi"),
        DISCOUNT("💰", "Giảm giá"),
        PREMIUM_REMINDER("⭐", "Premium");
        
        private final String icon;
        private final String displayName;
        
        NotificationType(String icon, String displayName) {
            this.icon = icon;
            this.displayName = displayName;
        }
        
        public String getIcon() { return icon; }
        public String getDisplayName() { return displayName; }
    }
}