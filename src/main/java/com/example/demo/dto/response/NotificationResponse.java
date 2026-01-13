package com.example.demo.dto.response;

import com.example.demo.models.Notification;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    
    private UUID id;
    private String title;
    private String message;
    private String type;
    private String typeIcon;
    private String typeDisplayName;
    private boolean isRead;
    private String actionUrl;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sentAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readAt;
    
    // Movie info if related
    private UUID movieId;
    private String movieTitle;
    private String movieThumb;
    
    // Metadata as parsed object
    private String metadata;
    
    // Time ago helper
    private String timeAgo;
    
    public static NotificationResponse fromEntity(Notification notification) {
        NotificationResponseBuilder builder = NotificationResponse.builder()
            .id(notification.getId())
            .title(notification.getTitle())
            .message(notification.getMessage())
            .type(notification.getType().name())
            .typeIcon(notification.getType().getIcon())
            .typeDisplayName(notification.getType().getDisplayName())
            .isRead(notification.isRead())
            .actionUrl(notification.getActionUrl())
            .sentAt(notification.getSentAt())
            .readAt(notification.getReadAt())
            .metadata(notification.getMetadata());
            
        // Add movie info if exists
        if (notification.getRelatedMovie() != null) {
            builder.movieId(notification.getRelatedMovie().getId())
                   .movieTitle(notification.getRelatedMovie().getTitle())
                   .movieThumb(notification.getRelatedMovie().getThumb_url());
        }
        
        return builder.build();
    }
    
    public String getFormattedMessage() {
        return String.format("%s %s", typeIcon, message);
    }
}