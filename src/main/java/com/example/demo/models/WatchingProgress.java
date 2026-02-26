package com.example.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "watching_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class WatchingProgress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "movie_id", nullable = false) 
    private String movieId;
    
    @Column(name = "movie_title")
    private String movieTitle;
    
    @Column(name = "current_time_seconds", nullable = false)
    private Integer currentTime = 0;
    
    @Column(name = "total_duration_seconds")
    private Integer totalDuration;
    
    @Column(name = "percentage")
    private Double percentage = 0.0;
    
    @Column(name = "is_completed")
    private Boolean isCompleted = false;
    
    @CreatedDate
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @LastModifiedDate
    @Column(name = "last_watched")
    private LocalDateTime lastWatched;
    
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Unique constraint for user-movie combination
    @Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "movie_id"})
    })
    public static class UniqueConstraints {}
    
    /**
     * Calculate và update percentage
     */
    public void calculatePercentage() {
        if (totalDuration != null && totalDuration > 0) {
            this.percentage = Math.round((double) currentTime / totalDuration * 100 * 10) / 10.0;
        }
    }
    
    /**
     * Mark as completed
     */
    public void markCompleted() {
        this.isCompleted = true;
        this.percentage = 100.0;
        this.currentTime = this.totalDuration != null ? this.totalDuration : this.currentTime;
    }
}