package com.rajveer.finance.goal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record GoalContributionResponse(
        Long id,
        Long goalId,
        BigDecimal amount,
        LocalDate contributionDate,
        String note,
        LocalDateTime createdAt
) {
}