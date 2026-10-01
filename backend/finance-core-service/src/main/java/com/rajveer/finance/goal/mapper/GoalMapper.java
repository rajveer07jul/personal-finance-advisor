package com.rajveer.finance.goal.mapper;

import com.rajveer.finance.common.enums.GoalStatus;
import com.rajveer.finance.goal.dto.GoalContributionRequest;
import com.rajveer.finance.goal.dto.GoalContributionResponse;
import com.rajveer.finance.goal.dto.GoalRequest;
import com.rajveer.finance.goal.dto.GoalResponse;
import com.rajveer.finance.goal.entity.FinancialGoal;
import com.rajveer.finance.goal.entity.GoalContribution;
import com.rajveer.finance.user.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class GoalMapper {

    private static final BigDecimal ONE_HUNDRED =
            new BigDecimal("100");

    public FinancialGoal toEntity(
            GoalRequest request,
            User user
    ) {
        return FinancialGoal.builder()
                .user(user)
                .name(request.name().trim())
                .description(
                        normalizeOptionalValue(
                                request.description()
                        )
                )
                .goalType(request.goalType())
                .targetAmount(request.targetAmount())
                .currentAmount(BigDecimal.ZERO)
                .targetDate(request.targetDate())
                .priority(request.priority())
                .status(GoalStatus.NOT_STARTED)
                .notes(
                        normalizeOptionalValue(
                                request.notes()
                        )
                )
                .build();
    }

    public void updateEntity(
            FinancialGoal goal,
            GoalRequest request
    ) {
        goal.setName(request.name().trim());
        goal.setDescription(
                normalizeOptionalValue(
                        request.description()
                )
        );
        goal.setGoalType(request.goalType());
        goal.setTargetAmount(request.targetAmount());
        goal.setTargetDate(request.targetDate());
        goal.setPriority(request.priority());
        goal.setNotes(
                normalizeOptionalValue(
                        request.notes()
                )
        );
    }

    public GoalContribution toContributionEntity(
            GoalContributionRequest request,
            FinancialGoal goal
    ) {
        return GoalContribution.builder()
                .goal(goal)
                .amount(request.amount())
                .contributionDate(
                        request.contributionDate()
                )
                .note(
                        normalizeOptionalValue(
                                request.note()
                        )
                )
                .build();
    }

    public GoalResponse toResponse(
            FinancialGoal goal
    ) {
        BigDecimal remainingAmount =
                calculateRemainingAmount(goal);

        BigDecimal progressPercentage =
                calculateProgressPercentage(goal);

        long remainingDays =
                calculateRemainingDays(
                        goal.getTargetDate()
                );

        return new GoalResponse(
                goal.getId(),
                goal.getName(),
                goal.getDescription(),
                goal.getGoalType(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                remainingAmount,
                progressPercentage,
                goal.getTargetDate(),
                remainingDays,
                goal.getPriority(),
                goal.getStatus(),
                goal.getNotes(),
                goal.getCreatedAt(),
                goal.getUpdatedAt()
        );
    }

    public GoalContributionResponse toContributionResponse(
            GoalContribution contribution
    ) {
        return new GoalContributionResponse(
                contribution.getId(),
                contribution.getGoal().getId(),
                contribution.getAmount(),
                contribution.getContributionDate(),
                contribution.getNote(),
                contribution.getCreatedAt()
        );
    }

    private BigDecimal calculateRemainingAmount(
            FinancialGoal goal
    ) {
        BigDecimal remaining =
                goal.getTargetAmount().subtract(
                        goal.getCurrentAmount()
                );

        return remaining.max(BigDecimal.ZERO);
    }

    private BigDecimal calculateProgressPercentage(
            FinancialGoal goal
    ) {
        if (goal.getTargetAmount().compareTo(
                BigDecimal.ZERO
        ) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal percentage =
                goal.getCurrentAmount()
                        .multiply(ONE_HUNDRED)
                        .divide(
                                goal.getTargetAmount(),
                                2,
                                RoundingMode.HALF_UP
                        );

        return percentage.min(ONE_HUNDRED);
    }

    private long calculateRemainingDays(
            LocalDate targetDate
    ) {
        long days = ChronoUnit.DAYS.between(
                LocalDate.now(),
                targetDate
        );

        return Math.max(days, 0);
    }

    private String normalizeOptionalValue(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}