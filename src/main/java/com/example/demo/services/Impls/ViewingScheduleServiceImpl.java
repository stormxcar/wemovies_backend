package com.example.demo.services.Impls;

import com.example.demo.config.root.ResourceNotFoundException;
import com.example.demo.dto.request.ViewingScheduleRequest;
import com.example.demo.dto.response.MovieDto;
import com.example.demo.dto.response.ViewingScheduleResponse;
import com.example.demo.enums.ScheduleStatus;
import com.example.demo.models.*;
import com.example.demo.models.auth.User;
import com.example.demo.repositories.MovieRepository;
import com.example.demo.repositories.ViewingScheduleRepository;
import com.example.demo.repositories.auth.UserRepository;
import com.example.demo.services.EmailService;
import com.example.demo.services.ViewingScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ViewingScheduleServiceImpl implements ViewingScheduleService {

    @Autowired
    private ViewingScheduleRepository viewingScheduleRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AsyncEmailService asyncEmailService;

    @Override
    @Transactional
    public ViewingScheduleResponse createSchedule(ViewingScheduleRequest request, User user) {
        // Validate movie exists
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        // Check if user already has a pending schedule for this movie
        if (viewingScheduleRepository.existsByUserAndMovieIdAndStatus(user, request.getMovieId(), ScheduleStatus.PENDING)) {
            throw new IllegalArgumentException("Bạn đã có lịch xem phim đang chờ xử lý cho bộ phim này");
        }

        ViewingSchedule schedule = ViewingSchedule.builder()
                .user(user)
                .movie(movie)
                .scheduledDateTime(request.getScheduledDateTime())
                .reminderEnabled(request.isReminderEnabled())
                .notes(request.getNotes())
                .status(ScheduleStatus.PENDING)
                .build();

        ViewingSchedule savedSchedule = viewingScheduleRepository.save(schedule);
        return mapToResponse(savedSchedule);
    }

    @Override
    public List<ViewingScheduleResponse> getUserSchedules(User user, ScheduleStatus status) {
        List<ViewingSchedule> schedules;
        if (status != null) {
            schedules = viewingScheduleRepository.findByUserAndStatusOrderByScheduledDateTimeAsc(user, status);
        } else {
            schedules = viewingScheduleRepository.findByUserAndStatusOrderByScheduledDateTimeAsc(user, ScheduleStatus.PENDING);
        }
        return schedules.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ViewingScheduleResponse updateSchedule(UUID scheduleId, ViewingScheduleRequest request, User user) {
        ViewingSchedule schedule = viewingScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch xem phim"));

        if (!schedule.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Bạn chỉ có thể cập nhật lịch của chính mình");
        }

        // Validate movie exists if movieId is provided
        if (request.getMovieId() != null) {
            Movie movie = movieRepository.findById(request.getMovieId())
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
            schedule.setMovie(movie);
        }

        if (request.getScheduledDateTime() != null) {
            schedule.setScheduledDateTime(request.getScheduledDateTime());
        }
        schedule.setReminderEnabled(request.isReminderEnabled());
        schedule.setNotes(request.getNotes());

        ViewingSchedule updatedSchedule = viewingScheduleRepository.save(schedule);
        return mapToResponse(updatedSchedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(UUID scheduleId, User user) {
        ViewingSchedule schedule = viewingScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch xem phim"));

        if (!schedule.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Bạn chỉ có thể xóa lịch của chính mình");
        }

        viewingScheduleRepository.delete(schedule);
    }

    @Override
    public boolean isMovieScheduledByUser(User user, UUID movieId) {
        return viewingScheduleRepository.existsByUserAndMovieIdAndStatus(user, movieId, ScheduleStatus.PENDING);
    }

    @Override
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderTime = now.plusMinutes(30); // Send reminder 30 minutes before

        List<ViewingSchedule> schedulesToRemind = viewingScheduleRepository
                .findPendingReminders(reminderTime, ScheduleStatus.PENDING);

        for (ViewingSchedule schedule : schedulesToRemind) {
            try {
                String subject = "Nhắc nhở: Phim đã lên lịch xem";
                String body = String.format(
                    "Xin chào %s,\n\nĐây là lời nhắc nhở rằng bạn đã lên lịch xem phim '%s' vào lúc %s.\n\nGhi chú: %s\n\nChúc bạn xem phim vui vẻ!",
                    schedule.getUser().getFullName(),
                    schedule.getMovie().getTitle(),
                    schedule.getScheduledDateTime(),
                    schedule.getNotes() != null ? schedule.getNotes() : "Không có ghi chú"
                );

                asyncEmailService.sendEmailAsync(schedule.getUser().getEmail(), subject, body);

                // Mark as completed or add a reminder sent flag if needed
                // For now, we'll just send the email

            } catch (Exception e) {
                // Log error but continue with other reminders
                System.err.println("Failed to send reminder for schedule " + schedule.getId() + ": " + e.getMessage());
            }
        }
    }

    private ViewingScheduleResponse mapToResponse(ViewingSchedule schedule) {
        MovieDto movieDto = new MovieDto(schedule.getMovie());

        return ViewingScheduleResponse.builder()
                .id(schedule.getId())
                .movie(movieDto)
                .scheduledDateTime(schedule.getScheduledDateTime())
                .reminderEnabled(schedule.isReminderEnabled())
                .status(schedule.getStatus())
                .notes(schedule.getNotes())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }
}