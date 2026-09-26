package com.rajveer.finance.expense.dto;

import com.rajveer.finance.common.enums.ExpenseCategory;
import com.rajveer.finance.common.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseFilterRequest(
        LocalDate startDate,
        LocalDate endDate,
        ExpenseCategory category,
        PaymentMethod paymentMethod,
        BigDecimal minimumAmount,
        BigDecimal maximumAmount,
        String search
) {
}