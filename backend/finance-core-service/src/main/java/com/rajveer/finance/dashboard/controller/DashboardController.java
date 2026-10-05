package com.rajveer.finance.dashboard.controller;

import com.rajveer.finance.common.response.ApiResponse;
import com.rajveer.finance.dashboard.dto.DashboardSummaryResponse;
import com.rajveer.finance.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>>
    getDashboardSummary(
            @AuthenticationPrincipal Jwt jwt,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate month
    ) {
        DashboardSummaryResponse response =
                dashboardService.getDashboardSummary(
                        extractUserId(jwt),
                        month
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Dashboard summary retrieved successfully",
                        response
                )
        );
    }

    private Long extractUserId(
            Jwt jwt
    ) {
        Number userId = jwt.getClaim("userId");

        if (userId == null) {
            throw new IllegalStateException(
                    "Authenticated token does not contain a user ID"
            );
        }

        return userId.longValue();
    }
}