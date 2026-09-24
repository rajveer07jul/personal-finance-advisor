package com.rajveer.finance.auth.service;

import com.rajveer.finance.auth.dto.AuthenticationResponse;
import com.rajveer.finance.auth.dto.LoginRequest;
import com.rajveer.finance.auth.dto.RefreshTokenRequest;
import com.rajveer.finance.auth.dto.RegisterRequest;
import com.rajveer.finance.auth.dto.TokenResponse;

public interface AuthenticationService {

    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse login(LoginRequest request);

    TokenResponse refresh(RefreshTokenRequest request);

    void logout(RefreshTokenRequest request);
}