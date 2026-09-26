package com.rajveer.finance.expense.service;

import com.rajveer.finance.common.response.PagedResponse;
import com.rajveer.finance.expense.dto.ExpenseFilterRequest;
import com.rajveer.finance.expense.dto.ExpenseRequest;
import com.rajveer.finance.expense.dto.ExpenseResponse;
import com.rajveer.finance.expense.dto.ExpenseSummaryResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ExpenseService {

    ExpenseResponse createExpense(
            Long userId,
            ExpenseRequest request
    );

    PagedResponse<ExpenseResponse> getExpenses(
            Long userId,
            ExpenseFilterRequest filter,
            Pageable pageable
    );

    ExpenseResponse getExpenseById(
            Long userId,
            Long expenseId
    );

    ExpenseResponse updateExpense(
            Long userId,
            Long expenseId,
            ExpenseRequest request
    );

    void deleteExpense(
            Long userId,
            Long expenseId
    );

    ExpenseSummaryResponse getExpenseSummary(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );
}