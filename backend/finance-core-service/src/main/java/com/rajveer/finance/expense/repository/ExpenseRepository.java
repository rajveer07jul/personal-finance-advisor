package com.rajveer.finance.expense.repository;

import com.rajveer.finance.common.enums.ExpenseCategory;
import com.rajveer.finance.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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

    @Query("""
            SELECT COALESCE(SUM(e.amount), 0)
            FROM Expense e
            WHERE e.user.id = :userId
              AND e.expenseDate BETWEEN :startDate AND :endDate
            """)
    BigDecimal sumAmountByUserAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            SELECT COALESCE(SUM(e.amount), 0)
            FROM Expense e
            WHERE e.user.id = :userId
              AND e.category = :category
              AND e.expenseDate BETWEEN :startDate AND :endDate
            """)
    BigDecimal sumAmountByUserCategoryAndDateRange(
            @Param("userId") Long userId,
            @Param("category") ExpenseCategory category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}