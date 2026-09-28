package com.rajveer.finance.budget.controller;

import com.rajveer.finance.budget.dto.BudgetRequest;
import com.rajveer.finance.budget.dto.BudgetResponse;
import com.rajveer.finance.budget.dto.BudgetSummaryResponse;
import com.rajveer.finance.budget.service.BudgetService;
import com.rajveer.finance.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<ApiResponse<BudgetResponse>>
    createBudget(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody BudgetRequest request
    ) {
        BudgetResponse response =
                budgetService.createBudget(
                        extractUserId(jwt),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Budget created successfully",
                                response
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BudgetResponse>>>
    getBudgetsForMonth(
            @AuthenticationPrincipal Jwt jwt,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate month
    ) {
        List<BudgetResponse> response =
                budgetService.getBudgetsForMonth(
                        extractUserId(jwt),
                        month
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Budgets retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<BudgetSummaryResponse>>
    getBudgetSummary(
            @AuthenticationPrincipal Jwt jwt,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate month
    ) {
        BudgetSummaryResponse response =
                budgetService.getBudgetSummary(
                        extractUserId(jwt),
                        month
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Budget summary retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<ApiResponse<BudgetResponse>>
    getBudgetById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long budgetId
    ) {
        BudgetResponse response =
                budgetService.getBudgetById(
                        extractUserId(jwt),
                        budgetId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Budget retrieved successfully",
                        response
                )
        );
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<ApiResponse<BudgetResponse>>
    updateBudget(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long budgetId,
            @Valid @RequestBody BudgetRequest request
    ) {
        BudgetResponse response =
                budgetService.updateBudget(
                        extractUserId(jwt),
                        budgetId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Budget updated successfully",
                        response
                )
        );
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long budgetId
    ) {
        budgetService.deleteBudget(
                extractUserId(jwt),
                budgetId
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Budget deleted successfully"
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
}