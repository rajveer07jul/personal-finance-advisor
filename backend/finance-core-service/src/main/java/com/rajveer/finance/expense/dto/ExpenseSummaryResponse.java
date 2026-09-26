package com.rajveer.finance.expense.dto;

import com.rajveer.finance.common.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record ExpenseSummaryResponse(
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalAmount,
        long totalTransactions,
        ExpenseCategory highestSpendingCategory,
        BigDecimal highestCategoryAmount,
        Map<ExpenseCategory, BigDecimal> categoryWiseAmounts
) {
}