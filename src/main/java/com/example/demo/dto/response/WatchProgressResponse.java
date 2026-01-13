package com.example.demo.dto.response;

import com.example.demo.dto.response.MovieDto;
import com.example.demo.enums.WatchStatus;
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
public class WatchProgressResponse {
    
    private UUID id;
    private MovieDto movie;
    private UUID currentEpisodeId;
    private String currentEpisodeTitle; // Tên tập hiện tại
    private Long currentTime; // Thời gian hiện tại (giây)
    private Long totalDuration; // Tổng thời lượng (giây)
    private Double watchPercentage; // Phần trăm đã xem
    private LocalDateTime lastWatched;
    private WatchStatus status;
    private Integer episodeNumber; // Số tập hiện tại
    private Integer totalEpisodes; // Tổng số tập
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Helper methods for frontend
    public String getFormattedCurrentTime() {
        if (currentTime == null) return "00:00";
        long hours = currentTime / 3600;
        long minutes = (currentTime % 3600) / 60;
        long seconds = currentTime % 60;
        
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
    
    public String getFormattedTotalDuration() {
        if (totalDuration == null) return "00:00";
        long hours = totalDuration / 3600;
        long minutes = (totalDuration % 3600) / 60;
        long seconds = totalDuration % 60;
        
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
    
    public String getProgressText() {
        if (episodeNumber != null && totalEpisodes != null) {
            return String.format("Tập %d/%d - %.1f%%", episodeNumber, totalEpisodes, watchPercentage);
        } else {
            return String.format("%.1f%%", watchPercentage);
        }
    }
}