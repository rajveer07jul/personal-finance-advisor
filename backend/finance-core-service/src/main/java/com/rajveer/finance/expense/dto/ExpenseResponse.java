package com.rajveer.finance.expense.dto;

import com.rajveer.finance.common.enums.ExpenseCategory;
import com.rajveer.finance.common.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseResponse(
        Long id,
        String title,
        String description,
        BigDecimal amount,
        ExpenseCategory category,
        PaymentMethod paymentMethod,
        LocalDate expenseDate,
        String merchantName,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}