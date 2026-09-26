package com.rajveer.finance.expense.dto;

import com.rajveer.finance.common.enums.ExpenseCategory;
import com.rajveer.finance.common.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(

        @NotBlank(message = "Expense title is required")
        @Size(
                max = 100,
                message = "Expense title must not exceed 100 characters"
        )
        String title,

        @Size(
                max = 500,
                message = "Description must not exceed 500 characters"
        )
        String description,

        @NotNull(message = "Expense amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Expense amount must be greater than zero"
        )
        BigDecimal amount,

        @NotNull(message = "Expense category is required")
        ExpenseCategory category,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        @NotNull(message = "Expense date is required")
        @PastOrPresent(
                message = "Expense date cannot be in the future"
        )
        LocalDate expenseDate,

        @Size(
                max = 100,
                message = "Merchant name must not exceed 100 characters"
        )
        String merchantName,

        @Size(
                max = 1000,
                message = "Notes must not exceed 1000 characters"
        )
        String notes
) {
}