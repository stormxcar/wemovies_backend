package com.example.demo.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewingScheduleRequest {

    @NotNull(message = "ID phim là bắt buộc")
    private UUID movieId;

    @NotNull(message = "Thời gian lên lịch là bắt buộc")
    @Future(message = "Thời gian lên lịch phải là trong tương lai")
    private LocalDateTime scheduledDateTime;

    @Builder.Default
    private boolean reminderEnabled = false;

    private String notes;
}