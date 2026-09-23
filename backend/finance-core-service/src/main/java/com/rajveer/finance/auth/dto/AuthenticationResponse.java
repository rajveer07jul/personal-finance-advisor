package com.rajveer.finance.auth.dto;

import com.rajveer.finance.common.enums.AccountStatus;
import com.rajveer.finance.common.enums.Role;

public record AuthenticationResponse(
        Long userId,
        String firstName,
        String lastName,
        String email,
        Role role,
        AccountStatus accountStatus,
        String accessToken,
        String refreshToken
) {

    public static AuthenticationResponse registrationResponse(
            Long userId,
            String firstName,
            String lastName,
            String email,
            Role role,
            AccountStatus accountStatus
    ) {
        return new AuthenticationResponse(
                userId,
                firstName,
                lastName,
                email,
                role,
                accountStatus,
                null,
                null
        );
    }
}