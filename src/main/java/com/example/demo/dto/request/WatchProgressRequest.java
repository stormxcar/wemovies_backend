package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

import java.util.UUID;

@Data
public class WatchProgressRequest {
    
    @NotNull(message = "Movie ID là bắt buộc")
    private UUID movieId;
    
    private UUID episodeId; // Optional, cho phim bộ
    
    @NotNull(message = "Thời gian hiện tại là bắt buộc")
    @Min(value = 0, message = "Thời gian không thể âm")
    private Long currentTime; // Thời gian hiện tại (giây)
    
    private Long totalDuration; // Tổng thời lượng (giây)
    
    private Integer episodeNumber; // Số tập hiện tại
    
    private Integer totalEpisodes; // Tổng số tập
    
    @Min(value = 0, message = "Phần trăm không thể âm")
    @Max(value = 100, message = "Phần trăm không thể lớn hơn 100")
    private Double watchPercentage; // Optional, sẽ tự tính nếu không có
}