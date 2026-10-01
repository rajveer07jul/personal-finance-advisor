package com.rajveer.finance.goal.service;

import com.rajveer.finance.common.enums.GoalStatus;
import com.rajveer.finance.common.enums.GoalType;
import com.rajveer.finance.exception.BusinessRuleException;
import com.rajveer.finance.exception.ResourceNotFoundException;
import com.rajveer.finance.goal.dto.GoalContributionRequest;
import com.rajveer.finance.goal.dto.GoalContributionResponse;
import com.rajveer.finance.goal.dto.GoalProgressResponse;
import com.rajveer.finance.goal.dto.GoalRequest;
import com.rajveer.finance.goal.dto.GoalResponse;
import com.rajveer.finance.goal.entity.FinancialGoal;
import com.rajveer.finance.goal.entity.GoalContribution;
import com.rajveer.finance.goal.mapper.GoalMapper;
import com.rajveer.finance.goal.repository.FinancialGoalRepository;
import com.rajveer.finance.goal.repository.GoalContributionRepository;
import com.rajveer.finance.user.entity.User;
import com.rajveer.finance.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private static final BigDecimal ONE_HUNDRED =
            new BigDecimal("100");

    private final FinancialGoalRepository goalRepository;
    private final GoalContributionRepository contributionRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;

    @Override
    @Transactional
    public GoalResponse createGoal(
            Long userId,
            GoalRequest request
    ) {
        User user = findUserById(userId);

        FinancialGoal goal =
                goalMapper.toEntity(request, user);

        FinancialGoal savedGoal =
                goalRepository.save(goal);

        return goalMapper.toResponse(savedGoal);
    }

    @Override
    @Transactional
    public List<GoalResponse> getGoals(
            Long userId,
            GoalStatus status,
            GoalType goalType
    ) {
        List<FinancialGoal> goals;

        if (status != null) {
            goals = goalRepository
                    .findAllByUserIdAndStatusOrderByTargetDateAsc(
                            userId,
                            status
                    );
        } else if (goalType != null) {
            goals = goalRepository
                    .findAllByUserIdAndGoalTypeOrderByTargetDateAsc(
                            userId,
                            goalType
                    );
        } else {
            goals = goalRepository
                    .findAllByUserIdOrderByPriorityDescTargetDateAsc(
                            userId
                    );
        }

        goals.forEach(this::updateCalculatedStatus);

        return goals.stream()
                .map(goalMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public GoalResponse getGoalById(
            Long userId,
            Long goalId
    ) {
        FinancialGoal goal =
                findGoalOwnedByUser(goalId, userId);

        updateCalculatedStatus(goal);

        return goalMapper.toResponse(goal);
    }

    @Override
    @Transactional
    public GoalResponse updateGoal(
            Long userId,
            Long goalId,
            GoalRequest request
    ) {
        FinancialGoal goal =
                findGoalOwnedByUser(goalId, userId);

        if (goal.isCancelled()) {
            throw new BusinessRuleException(
                    "Cancelled goals cannot be updated"
            );
        }

        if (request.targetAmount().compareTo(
                goal.getCurrentAmount()
        ) < 0) {
            throw new BusinessRuleException(
                    "Target amount cannot be less than the current saved amount"
            );
        }

        goalMapper.updateEntity(goal, request);

        updateCalculatedStatus(goal);

        FinancialGoal savedGoal =
                goalRepository.save(goal);

        return goalMapper.toResponse(savedGoal);
    }

    @Override
    @Transactional
    public void deleteGoal(
            Long userId,
            Long goalId
    ) {
        FinancialGoal goal =
                findGoalOwnedByUser(goalId, userId);

        goalRepository.delete(goal);
    }

    @Override
    @Transactional
    public GoalContributionResponse addContribution(
            Long userId,
            Long goalId,
            GoalContributionRequest request
    ) {
        FinancialGoal goal =
                findGoalOwnedByUser(goalId, userId);

        if (!goal.acceptsContributions()) {
            throw new BusinessRuleException(
                    "This goal does not currently accept contributions"
            );
        }

        BigDecimal remainingAmount =
                goal.getTargetAmount().subtract(
                        goal.getCurrentAmount()
                );

        if (request.amount().compareTo(
                remainingAmount
        ) > 0) {
            throw new BusinessRuleException(
                    "Contribution cannot exceed the remaining goal amount"
            );
        }

        GoalContribution contribution =
                goalMapper.toContributionEntity(
                        request,
                        goal
                );

        goal.addContribution(contribution);
        goal.addToCurrentAmount(request.amount());

        GoalContribution savedContribution =
                contributionRepository.save(contribution);

        goalRepository.save(goal);

        return goalMapper.toContributionResponse(
                savedContribution
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalContributionResponse> getContributions(
            Long userId,
            Long goalId
    ) {
        findGoalOwnedByUser(goalId, userId);

        return contributionRepository
                .findAllByGoalIdAndGoalUserIdOrderByContributionDateDescIdDesc(
                        goalId,
                        userId
                )
                .stream()
                .map(goalMapper::toContributionResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteContribution(
            Long userId,
            Long goalId,
            Long contributionId
    ) {
        FinancialGoal goal =
                findGoalOwnedByUser(goalId, userId);

        GoalContribution contribution =
                contributionRepository
                        .findByIdAndGoalIdAndGoalUserId(
                                contributionId,
                                goalId,
                                userId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Goal contribution",
                                        "id",
                                        contributionId
                                )
                        );

        BigDecimal contributionAmount =
                contribution.getAmount();

        goal.removeContribution(contribution);
        goal.subtractFromCurrentAmount(
                contributionAmount
        );

        contributionRepository.delete(contribution);
        goalRepository.save(goal);
    }

    @Override
    @Transactional
    public GoalProgressResponse getGoalProgress(
            Long userId,
            Long goalId
    ) {
        FinancialGoal goal =
                findGoalOwnedByUser(goalId, userId);

        updateCalculatedStatus(goal);

        BigDecimal remainingAmount =
                goal.getTargetAmount()
                        .subtract(goal.getCurrentAmount())
                        .max(BigDecimal.ZERO);

        BigDecimal progressPercentage =
                calculateProgressPercentage(goal);

        long remainingDays =
                calculateRemainingDays(
                        goal.getTargetDate()
                );

        BigDecimal recommendedMonthlyContribution =
                calculateRecommendedMonthlyContribution(
                        remainingAmount,
                        goal.getTargetDate()
                );

        return new GoalProgressResponse(
                goal.getId(),
                goal.getName(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                remainingAmount,
                progressPercentage,
                goal.getTargetDate(),
                remainingDays,
                recommendedMonthlyContribution,
                goal.getStatus()
        );
    }

    @Override
    @Transactional
    public GoalResponse pauseGoal(
            Long userId,
            Long goalId
    ) {
        FinancialGoal goal =
                findGoalOwnedByUser(goalId, userId);

        if (goal.isCompleted()) {
            throw new BusinessRuleException(
                    "Completed goals cannot be paused"
            );
        }

        if (goal.isCancelled()) {
            throw new BusinessRuleException(
                    "Cancelled goals cannot be paused"
            );
        }

        if (goal.isPaused()) {
            throw new BusinessRuleException(
                    "Goal is already paused"
            );
        }

        goal.setStatus(GoalStatus.PAUSED);

        return goalMapper.toResponse(
                goalRepository.save(goal)
        );
    }

    @Override
    @Transactional
    public GoalResponse resumeGoal(
            Long userId,
            Long goalId
    ) {
        FinancialGoal goal =
                findGoalOwnedByUser(goalId, userId);

        if (!goal.isPaused()) {
            throw new BusinessRuleException(
                    "Only paused goals can be resumed"
            );
        }

        goal.updateStatusAfterContribution();
        updateCalculatedStatus(goal);

        return goalMapper.toResponse(
                goalRepository.save(goal)
        );
    }

    @Override
    @Transactional
    public GoalResponse cancelGoal(
            Long userId,
            Long goalId
    ) {
        FinancialGoal goal =
                findGoalOwnedByUser(goalId, userId);

        if (goal.isCompleted()) {
            throw new BusinessRuleException(
                    "Completed goals cannot be cancelled"
            );
        }

        if (goal.isCancelled()) {
            throw new BusinessRuleException(
                    "Goal is already cancelled"
            );
        }

        goal.setStatus(GoalStatus.CANCELLED);

        return goalMapper.toResponse(
                goalRepository.save(goal)
        );
    }

    private FinancialGoal findGoalOwnedByUser(
            Long goalId,
            Long userId
    ) {
        return goalRepository
                .findByIdAndUserId(goalId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Financial goal",
                                "id",
                                goalId
                        )
                );
    }

    private User findUserById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User",
                                "id",
                                userId
                        )
                );
    }

    private void updateCalculatedStatus(
            FinancialGoal goal
    ) {
        if (goal.isPaused() || goal.isCancelled()) {
            return;
        }

        if (goal.getCurrentAmount().compareTo(
                goal.getTargetAmount()
        ) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
            return;
        }

        if (goal.getTargetDate().isBefore(
                LocalDate.now()
        )) {
            goal.setStatus(GoalStatus.OVERDUE);
            return;
        }

        if (goal.getCurrentAmount().compareTo(
                BigDecimal.ZERO
        ) > 0) {
            goal.setStatus(GoalStatus.IN_PROGRESS);
        } else {
            goal.setStatus(GoalStatus.NOT_STARTED);
        }
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

    private BigDecimal calculateRecommendedMonthlyContribution(
            BigDecimal remainingAmount,
            LocalDate targetDate
    ) {
        if (remainingAmount.compareTo(
                BigDecimal.ZERO
        ) <= 0) {
            return BigDecimal.ZERO;
        }

        long remainingMonths =
                ChronoUnit.MONTHS.between(
                        LocalDate.now()
                                .withDayOfMonth(1),
                        targetDate
                                .withDayOfMonth(1)
                );

        remainingMonths = Math.max(
                remainingMonths,
                1
        );

        return remainingAmount.divide(
                BigDecimal.valueOf(remainingMonths),
                2,
                RoundingMode.HALF_UP
        );
    }
}