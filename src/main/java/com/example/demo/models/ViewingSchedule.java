package com.example.demo.models;

import com.example.demo.enums.ScheduleStatus;
import com.example.demo.models.auth.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewingSchedule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Người dùng là bắt buộc")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    @NotNull(message = "Phim là bắt buộc")
    private Movie movie;

    @Column(name = "scheduled_date_time")
    @Future(message = "Thời gian lên lịch phải là trong tương lai")
    private LocalDateTime scheduledDateTime;

    @Column(name = "reminder_enabled", nullable = false)
    @Builder.Default
    private boolean reminderEnabled = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ScheduleStatus status = ScheduleStatus.PENDING;

    @Column(name = "notes", length = 500)
    private String notes;
}