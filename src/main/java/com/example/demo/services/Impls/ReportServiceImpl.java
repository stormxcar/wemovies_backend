package com.example.demo.services.Impls;

import com.example.demo.dto.response.ReportResponse;
import com.example.demo.repositories.HistoryRepository;
import com.example.demo.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private HistoryRepository historyRepository;

    @Override
    public ReportResponse getMovieViewsReport(String period, UUID movieId, boolean compare) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = getStartDate(now, period);
        List<Object[]> results = historyRepository.getMovieViewsByPeriod(movieId, start);

        List<Map<String, Object>> data = convertToMapList(results);
        ReportResponse.Comparison comparison = compare ? calculateComparison(start, now, () -> historyRepository.countViewsBetween(start, now)) : null;

        return new ReportResponse(data, comparison);
    }

    @Override
    public ReportResponse getAllMoviesViewsReport(String period, boolean compare) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = getStartDate(now, period);
        List<Object[]> results = historyRepository.getAllMovieViewsByPeriod(start);

        List<Map<String, Object>> data = convertToMapList(results);
        ReportResponse.Comparison comparison = compare ? calculateComparison(start, now, () -> historyRepository.countViewsBetween(start, now)) : null;

        return new ReportResponse(data, comparison);
    }

    @Override
    public ReportResponse getCategoryViewsReport(String period, UUID categoryId, boolean compare) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = getStartDate(now, period);
        List<Object[]> results = historyRepository.getCategoryViewsByPeriod(categoryId, start);

        List<Map<String, Object>> data = convertToMapList(results);
        ReportResponse.Comparison comparison = compare ? calculateComparison(start, now, () -> historyRepository.countViewsBetween(start, now)) : null;

        return new ReportResponse(data, comparison);
    }

    @Override
    public ReportResponse getUserActivityReport(String period, boolean compare) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = getStartDate(now, period);
        List<Object[]> results = historyRepository.getUserActivityByPeriod(start);

        List<Map<String, Object>> data = results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("date", row[0]);
            map.put("activeUsers", row[1]);
            map.put("totalViews", row[2]);
            return map;
        }).toList();

        ReportResponse.Comparison comparison = compare ? calculateComparison(start, now, () -> historyRepository.countViewsBetween(start, now)) : null;

        return new ReportResponse(data, comparison);
    }

    private LocalDateTime getStartDate(LocalDateTime now, String period) {
        return switch (period.toLowerCase()) {
            case "day" -> now.truncatedTo(ChronoUnit.DAYS);
            case "week" -> now.minusWeeks(1).plusDays(1).truncatedTo(ChronoUnit.DAYS);
            case "month" -> now.minusMonths(1).plusDays(1).truncatedTo(ChronoUnit.DAYS);
            case "year" -> now.minusYears(1).plusDays(1).truncatedTo(ChronoUnit.DAYS);
            default -> now.minusDays(7);
        };
    }

    private List<Map<String, Object>> convertToMapList(List<Object[]> results) {
        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("date", row[0]);
            map.put("views", row[1]);
            return map;
        }).toList();
    }

    private ReportResponse.Comparison calculateComparison(LocalDateTime start, LocalDateTime end, java.util.function.Supplier<Long> currentSupplier) {
        long currentTotal = currentSupplier.get();
        LocalDateTime prevStart = start.minus(end.toLocalDate().toEpochDay() - start.toLocalDate().toEpochDay() + 1, ChronoUnit.DAYS);
        LocalDateTime prevEnd = start.minusDays(1);
        long previousTotal = historyRepository.countViewsBetween(prevStart, prevEnd);

        double changePercent = previousTotal == 0 ? 0 : ((double) (currentTotal - previousTotal) / previousTotal) * 100;

        return new ReportResponse.Comparison(currentTotal, previousTotal, changePercent);
    }
}