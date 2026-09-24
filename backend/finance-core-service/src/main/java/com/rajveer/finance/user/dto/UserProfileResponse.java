package com.rajveer.finance.user.dto;

import com.rajveer.finance.common.enums.AccountStatus;
import com.rajveer.finance.common.enums.Role;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserProfileResponse(
        Long userId,
        String email,
        Role role,
        AccountStatus accountStatus,
        boolean emailVerified,
        String firstName,
        String lastName,
        String phoneNumber,
        String currencyCode,
        BigDecimal monthlyIncome,
        String timezone,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}