package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private List<Map<String, Object>> data; // e.g., [{date: "2025-12-24", views: 100}]
    private Comparison comparison; // So sánh với kỳ trước

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comparison {
        private long currentTotal;
        private long previousTotal;
        private double changePercent; // % thay đổi
    }
}