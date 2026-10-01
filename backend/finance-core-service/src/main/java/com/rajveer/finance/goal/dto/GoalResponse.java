package com.rajveer.finance.goal.dto;

import com.rajveer.finance.common.enums.GoalPriority;
import com.rajveer.finance.common.enums.GoalStatus;
import com.rajveer.finance.common.enums.GoalType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record GoalResponse(
        Long id,
        String name,
        String description,
        GoalType goalType,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        BigDecimal remainingAmount,
        BigDecimal progressPercentage,
        LocalDate targetDate,
        long remainingDays,
        GoalPriority priority,
        GoalStatus status,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}