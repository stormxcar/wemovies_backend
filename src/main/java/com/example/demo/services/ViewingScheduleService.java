package com.example.demo.services;

import com.example.demo.dto.request.ViewingScheduleRequest;
import com.example.demo.dto.response.ViewingScheduleResponse;
import com.example.demo.enums.ScheduleStatus;
import com.example.demo.models.auth.User;

import java.util.List;
import java.util.UUID;

public interface ViewingScheduleService {

    ViewingScheduleResponse createSchedule(ViewingScheduleRequest request, User user);

    List<ViewingScheduleResponse> getUserSchedules(User user, ScheduleStatus status);

    ViewingScheduleResponse updateSchedule(UUID scheduleId, ViewingScheduleRequest request, User user);

    void deleteSchedule(UUID scheduleId, User user);

    boolean isMovieScheduledByUser(User user, UUID movieId);

    void sendReminders();
}