package com.rajveer.finance.budget.mapper;

import com.rajveer.finance.budget.dto.BudgetRequest;
import com.rajveer.finance.budget.dto.BudgetResponse;
import com.rajveer.finance.budget.dto.BudgetStatusResponse;
import com.rajveer.finance.budget.entity.Budget;
import com.rajveer.finance.common.enums.BudgetPeriod;
import com.rajveer.finance.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BudgetMapper {

    public Budget toEntity(
            BudgetRequest request,
            User user
    ) {
        return Budget.builder()
                .user(user)
                .name(request.name().trim())
                .amount(request.amount())
                .period(BudgetPeriod.MONTHLY)
                .budgetMonth(normalizeMonth(request.budgetMonth()))
                .category(request.category())
                .alertThreshold(request.alertThreshold())
                .notes(normalizeOptionalValue(request.notes()))
                .build();
    }

    public void updateEntity(
            Budget budget,
            BudgetRequest request
    ) {
        budget.setName(request.name().trim());
        budget.setAmount(request.amount());
        budget.setPeriod(BudgetPeriod.MONTHLY);
        budget.setBudgetMonth(
                normalizeMonth(request.budgetMonth())
        );
        budget.setCategory(request.category());
        budget.setAlertThreshold(request.alertThreshold());
        budget.setNotes(
                normalizeOptionalValue(request.notes())
        );
    }

    public BudgetResponse toResponse(
            Budget budget,
            BudgetStatusResponse status
    ) {
        return new BudgetResponse(
                budget.getId(),
                budget.getName(),
                budget.getAmount(),
                budget.getPeriod(),
                budget.getBudgetMonth(),
                budget.getCategory(),
                budget.isOverallBudget(),
                budget.getAlertThreshold(),
                budget.getNotes(),
                status.spentAmount(),
                status.remainingAmount(),
                status.utilizationPercentage(),
                status.status(),
                budget.getCreatedAt(),
                budget.getUpdatedAt()
        );
    }

    private LocalDate normalizeMonth(LocalDate date) {
        return date.withDayOfMonth(1);
    }

    private String normalizeOptionalValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}