package com.rajveer.finance.budget.service;

import com.rajveer.finance.budget.dto.BudgetRequest;
import com.rajveer.finance.budget.dto.BudgetResponse;
import com.rajveer.finance.budget.dto.BudgetStatusResponse;
import com.rajveer.finance.budget.dto.BudgetSummaryResponse;
import com.rajveer.finance.budget.entity.Budget;
import com.rajveer.finance.budget.mapper.BudgetMapper;
import com.rajveer.finance.budget.repository.BudgetRepository;
import com.rajveer.finance.common.enums.BudgetStatus;
import com.rajveer.finance.common.enums.ExpenseCategory;
import com.rajveer.finance.exception.DuplicateResourceException;
import com.rajveer.finance.exception.ResourceNotFoundException;
import com.rajveer.finance.expense.repository.ExpenseRepository;
import com.rajveer.finance.user.entity.User;
import com.rajveer.finance.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private static final BigDecimal ONE_HUNDRED =
            new BigDecimal("100");

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final BudgetMapper budgetMapper;

    @Override
    @Transactional
    public BudgetResponse createBudget(
            Long userId,
            BudgetRequest request
    ) {
        User user = findUserById(userId);

        LocalDate normalizedMonth =
                normalizeMonth(request.budgetMonth());

        validateDuplicateBudget(
                userId,
                normalizedMonth,
                request.category(),
                null
        );

        Budget budget = budgetMapper.toEntity(
                request,
                user
        );

        Budget savedBudget =
                budgetRepository.save(budget);

        return buildBudgetResponse(savedBudget);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgetsForMonth(
            Long userId,
            LocalDate budgetMonth
    ) {
        LocalDate normalizedMonth =
                normalizeMonth(budgetMonth);

        return budgetRepository
                .findAllByUserIdAndBudgetMonthOrderByCategoryAsc(
                        userId,
                        normalizedMonth
                )
                .stream()
                .map(this::buildBudgetResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetResponse getBudgetById(
            Long userId,
            Long budgetId
    ) {
        Budget budget = findBudgetOwnedByUser(
                budgetId,
                userId
        );

        return buildBudgetResponse(budget);
    }

    @Override
    @Transactional
    public BudgetResponse updateBudget(
            Long userId,
            Long budgetId,
            BudgetRequest request
    ) {
        Budget budget = findBudgetOwnedByUser(
                budgetId,
                userId
        );

        LocalDate normalizedMonth =
                normalizeMonth(request.budgetMonth());

        validateDuplicateBudget(
                userId,
                normalizedMonth,
                request.category(),
                budgetId
        );

        budgetMapper.updateEntity(
                budget,
                request
        );

        Budget savedBudget =
                budgetRepository.save(budget);

        return buildBudgetResponse(savedBudget);
    }

    @Override
    @Transactional
    public void deleteBudget(
            Long userId,
            Long budgetId
    ) {
        Budget budget = findBudgetOwnedByUser(
                budgetId,
                userId
        );

        budgetRepository.delete(budget);
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetSummaryResponse getBudgetSummary(
            Long userId,
            LocalDate budgetMonth
    ) {
        LocalDate normalizedMonth =
                normalizeMonth(budgetMonth);

        List<BudgetResponse> budgets =
                getBudgetsForMonth(
                        userId,
                        normalizedMonth
                );

        BigDecimal totalBudgetAmount =
                calculateTotalBudgetAmount(budgets);

        BigDecimal totalSpentAmount =
                calculateMonthlySpending(
                        userId,
                        normalizedMonth
                );

        BigDecimal totalRemainingAmount =
                totalBudgetAmount.subtract(
                        totalSpentAmount
                );

        long exceededBudgets = budgets.stream()
                .filter(budget ->
                        budget.status()
                                == BudgetStatus.EXCEEDED
                )
                .count();

        long warningBudgets = budgets.stream()
                .filter(budget ->
                        budget.status()
                                == BudgetStatus.WARNING
                )
                .count();

        return new BudgetSummaryResponse(
                normalizedMonth,
                totalBudgetAmount,
                totalSpentAmount,
                totalRemainingAmount,
                budgets.size(),
                exceededBudgets,
                warningBudgets,
                budgets
        );
    }

    private BigDecimal calculateTotalBudgetAmount(
            List<BudgetResponse> budgets
    ) {
        return budgets.stream()
                .filter(BudgetResponse::overallBudget)
                .map(BudgetResponse::amount)
                .findFirst()
                .orElseGet(() ->
                        budgets.stream()
                                .map(BudgetResponse::amount)
                                .reduce(
                                        BigDecimal.ZERO,
                                        BigDecimal::add
                                )
                );
    }

    private BudgetResponse buildBudgetResponse(
            Budget budget
    ) {
        BudgetStatusResponse status =
                calculateBudgetStatus(budget);

        return budgetMapper.toResponse(
                budget,
                status
        );
    }

    private BudgetStatusResponse calculateBudgetStatus(
            Budget budget
    ) {
        LocalDate startDate =
                budget.getBudgetMonth();

        LocalDate endDate =
                startDate.withDayOfMonth(
                        startDate.lengthOfMonth()
                );

        BigDecimal spentAmount;

        if (budget.isOverallBudget()) {
            spentAmount =
                    expenseRepository
                            .sumAmountByUserAndDateRange(
                                    budget.getUser().getId(),
                                    startDate,
                                    endDate
                            );
        } else {
            spentAmount =
                    expenseRepository
                            .sumAmountByUserCategoryAndDateRange(
                                    budget.getUser().getId(),
                                    budget.getCategory(),
                                    startDate,
                                    endDate
                            );
        }

        if (spentAmount == null) {
            spentAmount = BigDecimal.ZERO;
        }

        BigDecimal remainingAmount =
                budget.getAmount().subtract(
                        spentAmount
                );

        BigDecimal utilizationPercentage =
                calculateUtilization(
                        spentAmount,
                        budget.getAmount()
                );

        BudgetStatus status =
                determineStatus(
                        spentAmount,
                        utilizationPercentage,
                        budget.getAlertThreshold()
                );

        return new BudgetStatusResponse(
                budget.getAmount(),
                spentAmount,
                remainingAmount,
                utilizationPercentage,
                status
        );
    }

    private BigDecimal calculateMonthlySpending(
            Long userId,
            LocalDate budgetMonth
    ) {
        LocalDate startDate =
                normalizeMonth(budgetMonth);

        LocalDate endDate =
                startDate.withDayOfMonth(
                        startDate.lengthOfMonth()
                );

        BigDecimal result =
                expenseRepository
                        .sumAmountByUserAndDateRange(
                                userId,
                                startDate,
                                endDate
                        );

        return result == null
                ? BigDecimal.ZERO
                : result;
    }

    private BigDecimal calculateUtilization(
            BigDecimal spentAmount,
            BigDecimal budgetAmount
    ) {
        if (budgetAmount.compareTo(
                BigDecimal.ZERO
        ) <= 0) {
            return BigDecimal.ZERO;
        }

        return spentAmount
                .multiply(ONE_HUNDRED)
                .divide(
                        budgetAmount,
                        2,
                        RoundingMode.HALF_UP
                );
    }

    private BudgetStatus determineStatus(
            BigDecimal spentAmount,
            BigDecimal utilizationPercentage,
            BigDecimal alertThreshold
    ) {
        if (spentAmount.compareTo(
                BigDecimal.ZERO
        ) == 0) {
            return BudgetStatus.NOT_STARTED;
        }

        if (utilizationPercentage.compareTo(
                ONE_HUNDRED
        ) > 0) {
            return BudgetStatus.EXCEEDED;
        }

        if (utilizationPercentage.compareTo(
                alertThreshold
        ) >= 0) {
            return BudgetStatus.WARNING;
        }

        return BudgetStatus.ON_TRACK;
    }

    private void validateDuplicateBudget(
            Long userId,
            LocalDate budgetMonth,
            ExpenseCategory category,
            Long excludedBudgetId
    ) {
        boolean exists;

        if (category == null) {
            exists = checkOverallBudgetExists(
                    userId,
                    budgetMonth,
                    excludedBudgetId
            );
        } else {
            exists = checkCategoryBudgetExists(
                    userId,
                    budgetMonth,
                    category,
                    excludedBudgetId
            );
        }

        if (exists) {
            String budgetType =
                    category == null
                            ? "overall"
                            : category.name();

            throw new DuplicateResourceException(
                    "A "
                            + budgetType
                            + " budget already exists for "
                            + budgetMonth
            );
        }
    }

    private boolean checkOverallBudgetExists(
            Long userId,
            LocalDate budgetMonth,
            Long excludedBudgetId
    ) {
        if (excludedBudgetId == null) {
            return budgetRepository
                    .existsByUserIdAndBudgetMonthAndCategoryIsNull(
                            userId,
                            budgetMonth
                    );
        }

        return budgetRepository
                .existsByUserIdAndBudgetMonthAndCategoryIsNullAndIdNot(
                        userId,
                        budgetMonth,
                        excludedBudgetId
                );
    }

    private boolean checkCategoryBudgetExists(
            Long userId,
            LocalDate budgetMonth,
            ExpenseCategory category,
            Long excludedBudgetId
    ) {
        if (excludedBudgetId == null) {
            return budgetRepository
                    .existsByUserIdAndBudgetMonthAndCategory(
                            userId,
                            budgetMonth,
                            category
                    );
        }

        return budgetRepository
                .existsByUserIdAndBudgetMonthAndCategoryAndIdNot(
                        userId,
                        budgetMonth,
                        category,
                        excludedBudgetId
                );
    }

    private Budget findBudgetOwnedByUser(
            Long budgetId,
            Long userId
    ) {
        return budgetRepository
                .findByIdAndUserId(
                        budgetId,
                        userId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Budget",
                                "id",
                                budgetId
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

    private LocalDate normalizeMonth(LocalDate date) {
        return date.withDayOfMonth(1);
    }
}