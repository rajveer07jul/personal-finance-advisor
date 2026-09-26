package com.rajveer.finance.expense.service;

import com.rajveer.finance.common.enums.ExpenseCategory;
import com.rajveer.finance.common.response.PagedResponse;
import com.rajveer.finance.exception.BusinessRuleException;
import com.rajveer.finance.exception.ResourceNotFoundException;
import com.rajveer.finance.expense.dto.ExpenseFilterRequest;
import com.rajveer.finance.expense.dto.ExpenseRequest;
import com.rajveer.finance.expense.dto.ExpenseResponse;
import com.rajveer.finance.expense.dto.ExpenseSummaryResponse;
import com.rajveer.finance.expense.entity.Expense;
import com.rajveer.finance.expense.mapper.ExpenseMapper;
import com.rajveer.finance.expense.repository.ExpenseRepository;
import com.rajveer.finance.expense.specification.ExpenseSpecification;
import com.rajveer.finance.user.entity.User;
import com.rajveer.finance.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final ExpenseMapper expenseMapper;

    @Override
    @Transactional
    public ExpenseResponse createExpense(
            Long userId,
            ExpenseRequest request
    ) {
        User user = findUserById(userId);

        Expense expense =
                expenseMapper.toEntity(request, user);

        Expense savedExpense =
                expenseRepository.save(expense);

        return expenseMapper.toResponse(savedExpense);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ExpenseResponse> getExpenses(
            Long userId,
            ExpenseFilterRequest filter,
            Pageable pageable
    ) {
        validateFilter(filter);

        Page<ExpenseResponse> responsePage =
                expenseRepository
                        .findAll(
                                ExpenseSpecification.build(
                                        userId,
                                        filter
                                ),
                                pageable
                        )
                        .map(expenseMapper::toResponse);

        return PagedResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(
            Long userId,
            Long expenseId
    ) {
        Expense expense =
                findExpenseOwnedByUser(expenseId, userId);

        return expenseMapper.toResponse(expense);
    }

    @Override
    @Transactional
    public ExpenseResponse updateExpense(
            Long userId,
            Long expenseId,
            ExpenseRequest request
    ) {
        Expense expense =
                findExpenseOwnedByUser(expenseId, userId);

        expenseMapper.updateEntity(expense, request);

        Expense savedExpense =
                expenseRepository.save(expense);

        return expenseMapper.toResponse(savedExpense);
    }

    @Override
    @Transactional
    public void deleteExpense(
            Long userId,
            Long expenseId
    ) {
        Expense expense =
                findExpenseOwnedByUser(expenseId, userId);

        expenseRepository.delete(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseSummaryResponse getExpenseSummary(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        validateDateRange(startDate, endDate);

        ExpenseFilterRequest filter =
                new ExpenseFilterRequest(
                        startDate,
                        endDate,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        List<Expense> expenses =
                expenseRepository.findAll(
                        ExpenseSpecification.build(
                                userId,
                                filter
                        )
                );

        BigDecimal totalAmount = expenses.stream()
                .map(Expense::getAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        Map<ExpenseCategory, BigDecimal>
                categoryWiseAmounts =
                new EnumMap<>(ExpenseCategory.class);

        for (Expense expense : expenses) {
            categoryWiseAmounts.merge(
                    expense.getCategory(),
                    expense.getAmount(),
                    BigDecimal::add
            );
        }

        ExpenseCategory highestCategory = null;
        BigDecimal highestCategoryAmount =
                BigDecimal.ZERO;

        for (Map.Entry<ExpenseCategory, BigDecimal> entry :
                categoryWiseAmounts.entrySet()) {

            if (entry.getValue().compareTo(
                    highestCategoryAmount
            ) > 0) {
                highestCategory = entry.getKey();
                highestCategoryAmount = entry.getValue();
            }
        }

        return new ExpenseSummaryResponse(
                startDate,
                endDate,
                totalAmount,
                expenses.size(),
                highestCategory,
                highestCategoryAmount,
                categoryWiseAmounts
        );
    }

    private User findUserById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User",
                                "id",
                                userId
                        )
                );
    }

    private Expense findExpenseOwnedByUser(
            Long expenseId,
            Long userId
    ) {
        return expenseRepository
                .findByIdAndUserId(expenseId, userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Expense",
                                "id",
                                expenseId
                        )
                );
    }

    private void validateFilter(
            ExpenseFilterRequest filter
    ) {
        validateDateRange(
                filter.startDate(),
                filter.endDate()
        );

        if (filter.minimumAmount() != null &&
                filter.minimumAmount()
                        .compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException(
                    "Minimum amount cannot be negative"
            );
        }

        if (filter.maximumAmount() != null &&
                filter.maximumAmount()
                        .compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException(
                    "Maximum amount cannot be negative"
            );
        }

        if (filter.minimumAmount() != null &&
                filter.maximumAmount() != null &&
                filter.minimumAmount().compareTo(
                        filter.maximumAmount()
                ) > 0) {
            throw new BusinessRuleException(
                    "Minimum amount cannot exceed maximum amount"
            );
        }
    }

    private void validateDateRange(
            LocalDate startDate,
            LocalDate endDate
    ) {
        if (startDate != null &&
                endDate != null &&
                startDate.isAfter(endDate)) {
            throw new BusinessRuleException(
                    "Start date cannot be after end date"
            );
        }
    }
}