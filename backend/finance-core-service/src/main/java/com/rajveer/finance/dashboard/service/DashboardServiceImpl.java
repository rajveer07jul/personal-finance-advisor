package com.rajveer.finance.dashboard.service;

import com.rajveer.finance.budget.dto.BudgetSummaryResponse;
import com.rajveer.finance.budget.service.BudgetService;
import com.rajveer.finance.common.enums.GoalStatus;
import com.rajveer.finance.dashboard.dto.DashboardSummaryResponse;
import com.rajveer.finance.dashboard.dto.FinancialHealthResponse;
import com.rajveer.finance.dashboard.dto.MonthlySpendingResponse;
import com.rajveer.finance.exception.ResourceNotFoundException;
import com.rajveer.finance.expense.repository.ExpenseRepository;
import com.rajveer.finance.goal.entity.FinancialGoal;
import com.rajveer.finance.goal.repository.FinancialGoalRepository;
import com.rajveer.finance.portfolio.dto.PortfolioSummaryResponse;
import com.rajveer.finance.portfolio.service.PortfolioService;
import com.rajveer.finance.user.entity.User;
import com.rajveer.finance.user.entity.UserProfile;
import com.rajveer.finance.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl
        implements DashboardService {

    private static final BigDecimal ONE_HUNDRED =
            new BigDecimal("100");

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final FinancialGoalRepository goalRepository;
    private final BudgetService budgetService;
    private final PortfolioService portfolioService;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary(
            Long userId,
            LocalDate month
    ) {
        User user = findUserById(userId);

        LocalDate normalizedMonth =
                normalizeMonth(month);

        MonthlySpendingResponse monthlySpending =
                buildMonthlySpending(
                        user,
                        normalizedMonth
                );

        BudgetSummaryResponse budgetSummary =
                budgetService.getBudgetSummary(
                        userId,
                        normalizedMonth
                );

        List<FinancialGoal> goals =
                goalRepository
                        .findAllByUserIdOrderByPriorityDescTargetDateAsc(
                                userId
                        );

        long completedGoals = goals.stream()
                .filter(goal ->
                        goal.getStatus()
                                == GoalStatus.COMPLETED
                )
                .count();

        long activeGoals = goals.stream()
                .filter(this::isActiveGoal)
                .count();

        BigDecimal totalGoalTargetAmount =
                goals.stream()
                        .map(FinancialGoal::getTargetAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalGoalSavedAmount =
                goals.stream()
                        .map(FinancialGoal::getCurrentAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        PortfolioSummaryResponse portfolioSummary =
                portfolioService.getPortfolioSummary(userId);

        FinancialHealthResponse financialHealth =
                calculateFinancialHealth(
                        monthlySpending,
                        budgetSummary,
                        goals,
                        portfolioSummary
                );

        UserProfile profile = user.getProfile();

        String userName =
                profile == null
                        ? user.getEmail()
                        : profile.getFullName();

        return new DashboardSummaryResponse(
                user.getId(),
                userName,
                normalizedMonth,
                monthlySpending,
                budgetSummary,
                goals.size(),
                activeGoals,
                completedGoals,
                scaleMoney(totalGoalTargetAmount),
                scaleMoney(totalGoalSavedAmount),
                portfolioSummary,
                financialHealth
        );
    }

    private MonthlySpendingResponse buildMonthlySpending(
            User user,
            LocalDate month
    ) {
        LocalDate startDate = normalizeMonth(month);

        LocalDate endDate =
                startDate.withDayOfMonth(
                        startDate.lengthOfMonth()
                );

        BigDecimal totalExpenses =
                expenseRepository
                        .sumAmountByUserAndDateRange(
                                user.getId(),
                                startDate,
                                endDate
                        );

        if (totalExpenses == null) {
            totalExpenses = BigDecimal.ZERO;
        }

        long totalTransactions =
                expenseRepository
                        .countByUserIdAndExpenseDateBetween(
                                user.getId(),
                                startDate,
                                endDate
                        );

        BigDecimal monthlyIncome =
                getMonthlyIncome(user);

        BigDecimal estimatedSavings =
                monthlyIncome.subtract(totalExpenses);

        BigDecimal savingsRate =
                calculateSavingsRate(
                        estimatedSavings,
                        monthlyIncome
                );

        return new MonthlySpendingResponse(
                startDate,
                scaleMoney(monthlyIncome),
                scaleMoney(totalExpenses),
                scaleMoney(estimatedSavings),
                savingsRate,
                totalTransactions
        );
    }

    private FinancialHealthResponse calculateFinancialHealth(
            MonthlySpendingResponse monthlySpending,
            BudgetSummaryResponse budgetSummary,
            List<FinancialGoal> goals,
            PortfolioSummaryResponse portfolioSummary
    ) {
        int score = 0;

        List<String> insights = new ArrayList<>();

        score += calculateSavingsScore(
                monthlySpending,
                insights
        );

        score += calculateBudgetScore(
                budgetSummary,
                insights
        );

        score += calculateGoalScore(
                goals,
                insights
        );

        score += calculatePortfolioScore(
                portfolioSummary,
                insights
        );

        score = Math.min(
                Math.max(score, 0),
                100
        );

        return new FinancialHealthResponse(
                score,
                determineHealthRating(score),
                insights
        );
    }

    private int calculateSavingsScore(
            MonthlySpendingResponse monthlySpending,
            List<String> insights
    ) {
        if (monthlySpending.monthlyIncome()
                .compareTo(BigDecimal.ZERO) <= 0) {

            insights.add(
                    "Add monthly income in your profile "
                            + "to improve savings analysis."
            );

            return 0;
        }

        BigDecimal savingsRate =
                monthlySpending.savingsRate();

        if (savingsRate.compareTo(
                new BigDecimal("20")
        ) >= 0) {
            insights.add(
                    "Your savings rate is at least 20%, "
                            + "which is a strong level."
            );

            return 30;
        }

        if (savingsRate.compareTo(
                new BigDecimal("10")
        ) >= 0) {
            insights.add(
                    "Your savings rate is positive, "
                            + "but increasing it toward 20% "
                            + "would strengthen your finances."
            );

            return 20;
        }

        if (savingsRate.compareTo(
                BigDecimal.ZERO
        ) > 0) {
            insights.add(
                    "Your savings rate is below 10%. "
                            + "Review discretionary expenses."
            );

            return 10;
        }

        insights.add(
                "Your monthly expenses are equal to "
                        + "or greater than your income."
        );

        return 0;
    }

    private int calculateBudgetScore(
            BudgetSummaryResponse budgetSummary,
            List<String> insights
    ) {
        if (budgetSummary.totalBudgets() == 0) {
            insights.add(
                    "Create a monthly budget to improve "
                            + "spending control."
            );

            return 0;
        }

        if (budgetSummary.exceededBudgets() > 0) {
            insights.add(
                    "One or more budgets have been exceeded."
            );

            return 10;
        }

        if (budgetSummary.warningBudgets() > 0) {
            insights.add(
                    "One or more budgets are near their limits."
            );

            return 20;
        }

        insights.add(
                "Your configured budgets are currently on track."
        );

        return 30;
    }

    private int calculateGoalScore(
            List<FinancialGoal> goals,
            List<String> insights
    ) {
        if (goals.isEmpty()) {
            insights.add(
                    "Create at least one financial goal "
                            + "to begin structured saving."
            );

            return 0;
        }

        boolean hasActiveGoal = goals.stream()
                .anyMatch(this::isActiveGoal);

        boolean hasCompletedGoal = goals.stream()
                .anyMatch(goal ->
                        goal.getStatus()
                                == GoalStatus.COMPLETED
                );

        if (hasCompletedGoal) {
            insights.add(
                    "You have completed at least one financial goal."
            );

            return 20;
        }

        if (hasActiveGoal) {
            insights.add(
                    "You have active financial goals in progress."
            );

            return 15;
        }

        insights.add(
                "Your financial goals are paused, "
                        + "cancelled, or overdue."
        );

        return 5;
    }

    private int calculatePortfolioScore(
            PortfolioSummaryResponse portfolioSummary,
            List<String> insights
    ) {
        if (portfolioSummary.totalAssets() == 0) {
            insights.add(
                    "Add portfolio assets to track investments "
                            + "and diversification."
            );

            return 0;
        }

        if (portfolioSummary.assetAllocations().size() >= 3) {
            insights.add(
                    "Your portfolio contains at least three "
                            + "asset categories."
            );

            return 20;
        }

        if (portfolioSummary.assetAllocations().size() == 2) {
            insights.add(
                    "Your portfolio has some diversification, "
                            + "but additional asset categories "
                            + "may reduce concentration."
            );

            return 15;
        }

        insights.add(
                "Your portfolio is concentrated in one "
                        + "asset category."
        );

        return 10;
    }

    private boolean isActiveGoal(
            FinancialGoal goal
    ) {
        return goal.getStatus()
                == GoalStatus.NOT_STARTED
                || goal.getStatus()
                == GoalStatus.IN_PROGRESS;
    }

    private BigDecimal getMonthlyIncome(
            User user
    ) {
        UserProfile profile = user.getProfile();

        if (profile == null ||
                profile.getMonthlyIncome() == null) {
            return BigDecimal.ZERO;
        }

        return profile.getMonthlyIncome();
    }

    private BigDecimal calculateSavingsRate(
            BigDecimal estimatedSavings,
            BigDecimal monthlyIncome
    ) {
        if (monthlyIncome.compareTo(
                BigDecimal.ZERO
        ) <= 0) {
            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }

        return estimatedSavings
                .multiply(ONE_HUNDRED)
                .divide(
                        monthlyIncome,
                        2,
                        RoundingMode.HALF_UP
                );
    }

    private String determineHealthRating(
            int score
    ) {
        if (score >= 80) {
            return "EXCELLENT";
        }

        if (score >= 60) {
            return "GOOD";
        }

        if (score >= 40) {
            return "FAIR";
        }

        return "NEEDS_ATTENTION";
    }

    private BigDecimal scaleMoney(
            BigDecimal value
    ) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }

        return value.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }

    private User findUserById(
            Long userId
    ) {
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

    private LocalDate normalizeMonth(
            LocalDate month
    ) {
        LocalDate selectedMonth =
                month == null
                        ? LocalDate.now()
                        : month;

        return selectedMonth.withDayOfMonth(1);
    }
}