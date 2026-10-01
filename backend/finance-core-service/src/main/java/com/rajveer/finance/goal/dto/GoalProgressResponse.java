package com.rajveer.finance.goal.dto;

import com.rajveer.finance.common.enums.GoalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalProgressResponse(
        Long goalId,
        String goalName,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        BigDecimal remainingAmount,
        BigDecimal progressPercentage,
        LocalDate targetDate,
        long remainingDays,
        BigDecimal recommendedMonthlyContribution,
        GoalStatus status
) {
}
