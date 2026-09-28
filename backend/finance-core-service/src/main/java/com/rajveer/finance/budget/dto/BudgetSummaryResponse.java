package com.rajveer.finance.budget.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BudgetSummaryResponse(
        LocalDate budgetMonth,
        BigDecimal totalBudgetAmount,
        BigDecimal totalSpentAmount,
        BigDecimal totalRemainingAmount,
        long totalBudgets,
        long exceededBudgets,
        long warningBudgets,
        List<BudgetResponse> budgets
) {
}