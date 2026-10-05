package com.rajveer.finance.dashboard.dto;

import java.util.List;

public record FinancialHealthResponse(
        int score,
        String rating,
        List<String> insights
) {
}