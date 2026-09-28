package com.rajveer.finance.budget.dto;

import com.rajveer.finance.common.enums.BudgetStatus;

import java.math.BigDecimal;

public record BudgetStatusResponse(
        BigDecimal budgetAmount,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        BigDecimal utilizationPercentage,
        BudgetStatus status
) {
}