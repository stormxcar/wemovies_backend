package com.example.demo.controllers;

import com.example.demo.dto.response.ReportResponse;
import com.example.demo.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('ADMIN')") // Chỉ admin truy cập
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/movies")
    public ResponseEntity<ReportResponse> getMovieViewsReport(
            @RequestParam String period,
            @RequestParam(required = false) UUID movieId,
            @RequestParam(defaultValue = "false") boolean compare) {
        ReportResponse response = movieId != null ?
                reportService.getMovieViewsReport(period, movieId, compare) :
                reportService.getAllMoviesViewsReport(period, compare);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<ReportResponse> getCategoryViewsReport(
            @RequestParam String period,
            @RequestParam UUID categoryId,
            @RequestParam(defaultValue = "false") boolean compare) {
        ReportResponse response = reportService.getCategoryViewsReport(period, categoryId, compare);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<ReportResponse> getUserActivityReport(
            @RequestParam String period,
            @RequestParam(defaultValue = "false") boolean compare) {
        ReportResponse response = reportService.getUserActivityReport(period, compare);
        return ResponseEntity.ok(response);
    }
}