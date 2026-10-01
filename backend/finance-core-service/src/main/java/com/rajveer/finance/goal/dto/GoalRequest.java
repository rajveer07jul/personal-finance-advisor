package com.rajveer.finance.goal.dto;

import com.rajveer.finance.common.enums.GoalPriority;
import com.rajveer.finance.common.enums.GoalType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalRequest(

        @NotBlank(message = "Goal name is required")
        @Size(
                max = 100,
                message = "Goal name must not exceed 100 characters"
        )
        String name,

        @Size(
                max = 500,
                message = "Goal description must not exceed 500 characters"
        )
        String description,

        @NotNull(message = "Goal type is required")
        GoalType goalType,

        @NotNull(message = "Target amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Target amount must be greater than zero"
        )
        BigDecimal targetAmount,

        @NotNull(message = "Target date is required")
        @Future(message = "Target date must be in the future")
        LocalDate targetDate,

        @NotNull(message = "Goal priority is required")
        GoalPriority priority,

        @Size(
                max = 500,
                message = "Goal notes must not exceed 500 characters"
        )
        String notes
) {
}