package com.rajveer.finance.budget.dto;

import com.rajveer.finance.common.enums.ExpenseCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BudgetRequest(

        @NotBlank(message = "Budget name is required")
        @Size(
                max = 100,
                message = "Budget name must not exceed 100 characters"
        )
        String name,

        @NotNull(message = "Budget amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Budget amount must be greater than zero"
        )
        BigDecimal amount,

        @NotNull(message = "Budget month is required")
        LocalDate budgetMonth,

        ExpenseCategory category,

        @NotNull(message = "Alert threshold is required")
        @DecimalMin(
                value = "1.00",
                message = "Alert threshold must be at least 1"
        )
        @DecimalMax(
                value = "100.00",
                message = "Alert threshold cannot exceed 100"
        )
        BigDecimal alertThreshold,

        @Size(
                max = 500,
                message = "Budget notes must not exceed 500 characters"
        )
        String notes
) {
}