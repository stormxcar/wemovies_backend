package com.example.demo.dto;

import com.example.demo.enums.WatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for watch progress data
 * Used for API responses and Redis caching
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchProgressResponse {
    
    private UUID id;
    private UUID movieId;
    private String movieTitle;
    private Integer currentTime;
    private Integer totalDuration;
    private Double watchPercentage;
    private WatchStatus status;
    private Integer episodeNumber;
    private Integer totalEpisodes;
    private UUID currentEpisodeId;
    private String currentEpisodeTitle;
    private LocalDateTime lastWatched;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Check if movie is completed (>= 90% watched)
     */
    public boolean isCompleted() {
        return watchPercentage != null && watchPercentage >= 90.0;
    }
    
    /**
     * Get remaining time in seconds
     */
    public Integer getRemainingTime() {
        if (totalDuration == null || currentTime == null) {
            return null;
        }
        return Math.max(0, totalDuration - currentTime);
    }
    
    /**
     * Get formatted watch percentage
     */
    public String getFormattedWatchPercentage() {
        if (watchPercentage == null) {
            return "0%";
        }
        return String.format("%.1f%%", watchPercentage);
    }
}