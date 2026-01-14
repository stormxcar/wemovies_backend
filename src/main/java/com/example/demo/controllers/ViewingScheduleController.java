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
            @PathVariable String scheduleId,
            @Valid @RequestBody ViewingScheduleRequest request,
            Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        UUID scheduleUUID;
        try {
            scheduleUUID = UUID.fromString(scheduleId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid schedule ID format: " + scheduleId);
        }
        
        ViewingScheduleResponse response = viewingScheduleService.updateSchedule(scheduleUUID, request, user);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<String> deleteSchedule(
            @PathVariable String scheduleId,
            Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        UUID scheduleUUID;
        try {
            scheduleUUID = UUID.fromString(scheduleId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid schedule ID format: " + scheduleId);
        }
        
        viewingScheduleService.deleteSchedule(scheduleUUID, user);
        return ResponseEntity.ok("Xóa lịch xem phim thành công");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/check/{movieId}")
    public ResponseEntity<Boolean> isMovieScheduled(
            @PathVariable String movieId,
            Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        UUID movieUUID;
        try {
            movieUUID = UUID.fromString(movieId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid movie ID format: " + movieId);
        }
        
        boolean isScheduled = viewingScheduleService.isMovieScheduledByUser(user, movieUUID);
        return ResponseEntity.ok(isScheduled);
    }
    
    // Watch Later functionality
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/watch-later")
    public ResponseEntity<ViewingScheduleResponse> addToWatchLater(
            @RequestBody Map<String, String> request, 
            Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        String movieIdStr = request.get("movieId");
        if (movieIdStr == null || movieIdStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Movie ID is required");
        }
        
        UUID movieId;
        try {
            movieId = UUID.fromString(movieIdStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid movie ID format: " + movieIdStr);
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
            @PathVariable String movieId, 
            Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        UUID movieUUID;
        try {
            movieUUID = UUID.fromString(movieId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid movie ID format: " + movieId);
        }
        
        viewingScheduleService.removeFromWatchLater(movieUUID, user);
        return ResponseEntity.ok("Đã xóa khỏi danh sách xem sau");
    }
}