package com.rajveer.finance.auth.controller;

import com.rajveer.finance.auth.dto.AuthenticationResponse;
import com.rajveer.finance.auth.dto.LoginRequest;
import com.rajveer.finance.auth.dto.RefreshTokenRequest;
import com.rajveer.finance.auth.dto.RegisterRequest;
import com.rajveer.finance.auth.dto.TokenResponse;
import com.rajveer.finance.auth.service.AuthenticationService;
import com.rajveer.finance.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthenticationResponse response =
                authenticationService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "User registered successfully",
                                response
                        )
                );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthenticationResponse response =
                authenticationService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Login successful",
                        response
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        TokenResponse response =
                authenticationService.refresh(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Tokens refreshed successfully",
                        response
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        authenticationService.logout(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Logout successful"
                )
        );
    }
}