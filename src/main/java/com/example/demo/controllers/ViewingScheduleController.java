package com.example.demo.controllers;

import com.example.demo.dto.request.ViewingScheduleRequest;
import com.example.demo.dto.response.ViewingScheduleResponse;
import com.example.demo.enums.ScheduleStatus;
import com.example.demo.models.auth.User;
import com.example.demo.repositories.auth.UserRepository;
import com.example.demo.services.ViewingScheduleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/schedules")
public class ViewingScheduleController {

    @Autowired
    private ViewingScheduleService viewingScheduleService;

    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ViewingScheduleResponse> createSchedule(
            @Valid @RequestBody ViewingScheduleRequest request,
            Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        ViewingScheduleResponse response = viewingScheduleService.createSchedule(request, user);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<ViewingScheduleResponse>> getUserSchedules(
            @RequestParam(required = false) ScheduleStatus status,
            Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        List<ViewingScheduleResponse> schedules = viewingScheduleService.getUserSchedules(user, status);
        return ResponseEntity.ok(schedules);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{scheduleId}")
    public ResponseEntity<ViewingScheduleResponse> updateSchedule(
            @PathVariable UUID scheduleId,
            @Valid @RequestBody ViewingScheduleRequest request,
            Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        ViewingScheduleResponse response = viewingScheduleService.updateSchedule(scheduleId, request, user);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<String> deleteSchedule(
            @PathVariable UUID scheduleId,
            Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        viewingScheduleService.deleteSchedule(scheduleId, user);
        return ResponseEntity.ok("Xóa lịch xem phim thành công");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/check/{movieId}")
    public ResponseEntity<Boolean> isMovieScheduled(
            @PathVariable UUID movieId,
            Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        boolean isScheduled = viewingScheduleService.isMovieScheduledByUser(user, movieId);
        return ResponseEntity.ok(isScheduled);
    }
    
    // Watch Later functionality
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/watch-later")
    public ResponseEntity<ViewingScheduleResponse> addToWatchLater(
            @RequestBody Map<String, UUID> request, 
            Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        UUID movieId = request.get("movieId");
        if (movieId == null) {
            throw new IllegalArgumentException("Movie ID is required");
        }
        ViewingScheduleResponse response = viewingScheduleService.addToWatchLater(movieId, user);
        return ResponseEntity.ok(response);
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/watch-later")
    public ResponseEntity<List<ViewingScheduleResponse>> getWatchLaterList(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        List<ViewingScheduleResponse> watchLaterList = viewingScheduleService.getWatchLaterList(user);
        return ResponseEntity.ok(watchLaterList);
    }
    
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/watch-later/{movieId}")
    public ResponseEntity<String> removeFromWatchLater(
            @PathVariable UUID movieId, 
            Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        viewingScheduleService.removeFromWatchLater(movieId, user);
        return ResponseEntity.ok("Đã xóa khỏi danh sách xem sau");
    }
}