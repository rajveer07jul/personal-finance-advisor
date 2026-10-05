package com.rajveer.finance.dashboard.service;

import com.rajveer.finance.dashboard.dto.DashboardSummaryResponse;

import java.time.LocalDate;

public interface DashboardService {

    DashboardSummaryResponse getDashboardSummary(
            Long userId,
            LocalDate month
    );
}