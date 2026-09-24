package com.rajveer.finance.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long accessTokenExpiresIn
) {

    public static TokenResponse bearer(
            String accessToken,
            String refreshToken,
            long accessTokenExpiresIn
    ) {
        return new TokenResponse(
                accessToken,
                refreshToken,
                "Bearer",
                accessTokenExpiresIn
        );
    }
}