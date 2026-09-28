package com.rajveer.finance.budget.service;

import com.rajveer.finance.budget.dto.BudgetRequest;
import com.rajveer.finance.budget.dto.BudgetResponse;
import com.rajveer.finance.budget.dto.BudgetSummaryResponse;

import java.time.LocalDate;
import java.util.List;

public interface BudgetService {

    BudgetResponse createBudget(
            Long userId,
            BudgetRequest request
    );

    List<BudgetResponse> getBudgetsForMonth(
            Long userId,
            LocalDate budgetMonth
    );

    BudgetResponse getBudgetById(
            Long userId,
            Long budgetId
    );

    BudgetResponse updateBudget(
            Long userId,
            Long budgetId,
            BudgetRequest request
    );

    void deleteBudget(
            Long userId,
            Long budgetId
    );

    BudgetSummaryResponse getBudgetSummary(
            Long userId,
            LocalDate budgetMonth
    );
}