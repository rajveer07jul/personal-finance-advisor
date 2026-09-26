package com.rajveer.finance.expense.repository;

import com.rajveer.finance.common.enums.ExpenseCategory;
import com.rajveer.finance.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.Optional;

public interface ExpenseRepository
        extends JpaRepository<Expense, Long>,
        JpaSpecificationExecutor<Expense> {

    Optional<Expense> findByIdAndUserId(
            Long expenseId,
            Long userId
    );

    boolean existsByIdAndUserId(
            Long expenseId,
            Long userId
    );

    long countByUserId(Long userId);

    long countByUserIdAndExpenseDateBetween(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    long countByUserIdAndCategory(
            Long userId,
            ExpenseCategory category
    );
}