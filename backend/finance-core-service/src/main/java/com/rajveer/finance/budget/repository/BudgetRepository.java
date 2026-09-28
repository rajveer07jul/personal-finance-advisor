package com.rajveer.finance.budget.repository;

import com.rajveer.finance.budget.entity.Budget;
import com.rajveer.finance.common.enums.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository
        extends JpaRepository<Budget, Long> {

    Optional<Budget> findByIdAndUserId(
            Long budgetId,
            Long userId
    );

    List<Budget> findAllByUserIdAndBudgetMonthOrderByCategoryAsc(
            Long userId,
            LocalDate budgetMonth
    );

    boolean existsByUserIdAndBudgetMonthAndCategory(
            Long userId,
            LocalDate budgetMonth,
            ExpenseCategory category
    );

    boolean existsByUserIdAndBudgetMonthAndCategoryIsNull(
            Long userId,
            LocalDate budgetMonth
    );

    boolean existsByUserIdAndBudgetMonthAndCategoryAndIdNot(
            Long userId,
            LocalDate budgetMonth,
            ExpenseCategory category,
            Long budgetId
    );

    boolean existsByUserIdAndBudgetMonthAndCategoryIsNullAndIdNot(
            Long userId,
            LocalDate budgetMonth,
            Long budgetId
    );

    long countByUserId(Long userId);
}