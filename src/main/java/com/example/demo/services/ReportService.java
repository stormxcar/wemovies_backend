package com.example.demo.services;

import com.example.demo.dto.response.ReportResponse;

import java.util.UUID;

public interface ReportService {
    ReportResponse getMovieViewsReport(String period, UUID movieId, boolean compare);
    ReportResponse getAllMoviesViewsReport(String period, boolean compare);
    ReportResponse getCategoryViewsReport(String period, UUID categoryId, boolean compare);
    ReportResponse getUserActivityReport(String period, boolean compare);
}