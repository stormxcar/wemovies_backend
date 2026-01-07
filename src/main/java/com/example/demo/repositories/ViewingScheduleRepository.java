package com.example.demo.repositories;

import com.example.demo.enums.ScheduleStatus;
import com.example.demo.models.ViewingSchedule;
import com.example.demo.models.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ViewingScheduleRepository extends JpaRepository<ViewingSchedule, UUID> {

    List<ViewingSchedule> findByUserAndStatusOrderByScheduledDateTimeAsc(User user, ScheduleStatus status);

    List<ViewingSchedule> findByUserAndScheduledDateTimeBetween(User user, LocalDateTime start, LocalDateTime end);

    @Query("SELECT vs FROM ViewingSchedule vs WHERE vs.scheduledDateTime <= :now AND vs.status = :status")
    List<ViewingSchedule> findPendingReminders(@Param("now") LocalDateTime now, @Param("status") ScheduleStatus status);

    boolean existsByUserAndMovieIdAndStatus(User user, UUID movieId, ScheduleStatus status);
}