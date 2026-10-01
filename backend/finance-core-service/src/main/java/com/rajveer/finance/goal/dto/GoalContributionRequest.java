package com.rajveer.finance.goal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalContributionRequest(

        @NotNull(message = "Contribution amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Contribution amount must be greater than zero"
        )
        BigDecimal amount,

        @NotNull(message = "Contribution date is required")
        @PastOrPresent(
                message = "Contribution date cannot be in the future"
        )
        LocalDate contributionDate,

        @Size(
                max = 300,
                message = "Contribution note must not exceed 300 characters"
        )
        String note
) {
}