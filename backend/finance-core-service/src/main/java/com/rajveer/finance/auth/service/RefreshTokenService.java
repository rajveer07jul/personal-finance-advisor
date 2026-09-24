package com.rajveer.finance.auth.service;

import com.rajveer.finance.user.entity.User;

public interface RefreshTokenService {

    String createRefreshToken(User user);

    User validateAndRotate(String refreshToken);

    void revokeRefreshToken(String refreshToken);
}