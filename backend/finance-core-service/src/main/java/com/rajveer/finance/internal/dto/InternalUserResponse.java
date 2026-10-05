package com.rajveer.finance.internal.dto;

import com.rajveer.finance.common.enums.AccountStatus;
import com.rajveer.finance.common.enums.Role;

import java.time.LocalDateTime;

public record InternalUserResponse(
        Long userId,
        String email,
        String firstName,
        String lastName,
        String fullName,
        Role role,
        AccountStatus accountStatus,
        boolean emailVerified,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}