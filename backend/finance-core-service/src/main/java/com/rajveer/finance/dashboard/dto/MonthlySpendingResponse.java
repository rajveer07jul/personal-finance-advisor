package com.rajveer.finance.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MonthlySpendingResponse(
        LocalDate month,
        BigDecimal monthlyIncome,
        BigDecimal totalExpenses,
        BigDecimal estimatedSavings,
        BigDecimal savingsRate,
        long totalTransactions
) {
}