package com.rajveer.finance.dashboard.dto;

import com.rajveer.finance.budget.dto.BudgetSummaryResponse;
import com.rajveer.finance.portfolio.dto.PortfolioSummaryResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DashboardSummaryResponse(
        Long userId,
        String userName,
        LocalDate generatedForMonth,
        MonthlySpendingResponse monthlySpending,
        BudgetSummaryResponse budgetSummary,
        long totalGoals,
        long activeGoals,
        long completedGoals,
        BigDecimal totalGoalTargetAmount,
        BigDecimal totalGoalSavedAmount,
        PortfolioSummaryResponse portfolioSummary,
        FinancialHealthResponse financialHealth
) {
}