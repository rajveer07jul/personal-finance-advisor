package com.rajveer.finance.expense.mapper;

import com.rajveer.finance.expense.dto.ExpenseRequest;
import com.rajveer.finance.expense.dto.ExpenseResponse;
import com.rajveer.finance.expense.entity.Expense;
import com.rajveer.finance.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {

    public Expense toEntity(
            ExpenseRequest request,
            User user
    ) {
        return Expense.builder()
                .user(user)
                .title(request.title().trim())
                .description(
                        normalizeOptionalValue(request.description())
                )
                .amount(request.amount())
                .category(request.category())
                .paymentMethod(request.paymentMethod())
                .expenseDate(request.expenseDate())
                .merchantName(
                        normalizeOptionalValue(request.merchantName())
                )
                .notes(
                        normalizeOptionalValue(request.notes())
                )
                .build();
    }

    public void updateEntity(
            Expense expense,
            ExpenseRequest request
    ) {
        expense.setTitle(request.title().trim());
        expense.setDescription(
                normalizeOptionalValue(request.description())
        );
        expense.setAmount(request.amount());
        expense.setCategory(request.category());
        expense.setPaymentMethod(request.paymentMethod());
        expense.setExpenseDate(request.expenseDate());
        expense.setMerchantName(
                normalizeOptionalValue(request.merchantName())
        );
        expense.setNotes(
                normalizeOptionalValue(request.notes())
        );
    }

    public ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getTitle(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getPaymentMethod(),
                expense.getExpenseDate(),
                expense.getMerchantName(),
                expense.getNotes(),
                expense.getCreatedAt(),
                expense.getUpdatedAt()
        );
    }

    private String normalizeOptionalValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}