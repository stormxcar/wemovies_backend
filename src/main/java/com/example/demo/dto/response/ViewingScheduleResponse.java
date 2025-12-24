package com.example.demo.dto.response;

import com.example.demo.dto.response.MovieDto;
import com.example.demo.enums.ScheduleStatus;
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
public class ViewingScheduleResponse {

    private UUID id;
    private MovieDto movie;
    private LocalDateTime scheduledDateTime;
    private boolean reminderEnabled;
    private ScheduleStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}