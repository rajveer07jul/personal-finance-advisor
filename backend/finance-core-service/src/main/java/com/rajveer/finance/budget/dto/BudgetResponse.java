package com.rajveer.finance.budget.dto;

import com.rajveer.finance.common.enums.BudgetPeriod;
import com.rajveer.finance.common.enums.BudgetStatus;
import com.rajveer.finance.common.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BudgetResponse(
        Long id,
        String name,
        BigDecimal amount,
        BudgetPeriod period,
        LocalDate budgetMonth,
        ExpenseCategory category,
        boolean overallBudget,
        BigDecimal alertThreshold,
        String notes,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        BigDecimal utilizationPercentage,
        BudgetStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}