package com.rajveer.finance.expense.controller;

import com.rajveer.finance.common.enums.ExpenseCategory;
import com.rajveer.finance.common.enums.PaymentMethod;
import com.rajveer.finance.common.response.ApiResponse;
import com.rajveer.finance.common.response.PagedResponse;
import com.rajveer.finance.expense.dto.ExpenseFilterRequest;
import com.rajveer.finance.expense.dto.ExpenseRequest;
import com.rajveer.finance.expense.dto.ExpenseResponse;
import com.rajveer.finance.expense.dto.ExpenseSummaryResponse;
import com.rajveer.finance.expense.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of(
                    "expenseDate",
                    "amount",
                    "title",
                    "category",
                    "createdAt"
            );

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>>
    createExpense(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ExpenseRequest request
    ) {
        ExpenseResponse response =
                expenseService.createExpense(
                        extractUserId(jwt),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Expense created successfully",
                                response
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ExpenseResponse>>>
    getExpenses(
            @AuthenticationPrincipal Jwt jwt,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false)
            ExpenseCategory category,

            @RequestParam(required = false)
            PaymentMethod paymentMethod,

            @RequestParam(required = false)
            BigDecimal minimumAmount,

            @RequestParam(required = false)
            BigDecimal maximumAmount,

            @RequestParam(required = false)
            String search,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "expenseDate")
            String sortBy,

            @RequestParam(defaultValue = "desc")
            String sortDirection
    ) {
        validatePagination(page, size);
        validateSortField(sortBy);

        Sort.Direction direction =
                "asc".equalsIgnoreCase(sortDirection)
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortBy)
        );

        ExpenseFilterRequest filter =
                new ExpenseFilterRequest(
                        startDate,
                        endDate,
                        category,
                        paymentMethod,
                        minimumAmount,
                        maximumAmount,
                        search
                );

        PagedResponse<ExpenseResponse> response =
                expenseService.getExpenses(
                        extractUserId(jwt),
                        filter,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Expenses retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseResponse>>
    getExpenseById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long expenseId
    ) {
        ExpenseResponse response =
                expenseService.getExpenseById(
                        extractUserId(jwt),
                        expenseId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Expense retrieved successfully",
                        response
                )
        );
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseResponse>>
    updateExpense(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long expenseId,
            @Valid @RequestBody ExpenseRequest request
    ) {
        ExpenseResponse response =
                expenseService.updateExpense(
                        extractUserId(jwt),
                        expenseId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Expense updated successfully",
                        response
                )
        );
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long expenseId
    ) {
        expenseService.deleteExpense(
                extractUserId(jwt),
                expenseId
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Expense deleted successfully"
                )
        );
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<ExpenseSummaryResponse>>
    getExpenseSummary(
            @AuthenticationPrincipal Jwt jwt,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        ExpenseSummaryResponse response =
                expenseService.getExpenseSummary(
                        extractUserId(jwt),
                        startDate,
                        endDate
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Expense summary retrieved successfully",
                        response
                )
        );
    }

    private Long extractUserId(Jwt jwt) {
        Number userId = jwt.getClaim("userId");

        if (userId == null) {
            throw new IllegalStateException(
                    "Authenticated token does not contain a user ID"
            );
        }

        return userId.longValue();
    }

    private void validatePagination(
            int page,
            int size
    ) {
        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100"
            );
        }
    }

    private void validateSortField(String sortBy) {
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Unsupported expense sort field: " + sortBy
            );
        }
    }
}