package com.rajveer.finance.user.controller;

import com.rajveer.finance.common.response.ApiResponse;
import com.rajveer.finance.user.dto.ChangePasswordRequest;
import com.rajveer.finance.user.dto.UserProfileRequest;
import com.rajveer.finance.user.dto.UserProfileResponse;
import com.rajveer.finance.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>>
    getCurrentUserProfile(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = extractUserId(jwt);

        UserProfileResponse response =
                userService.getCurrentUserProfile(userId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User profile retrieved successfully",
                        response
                )
        );
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>>
    updateCurrentUserProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UserProfileRequest request
    ) {
        Long userId = extractUserId(jwt);

        UserProfileResponse response =
                userService.updateCurrentUserProfile(
                        userId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User profile updated successfully",
                        response
                )
        );
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        Long userId = extractUserId(jwt);

        userService.changePassword(userId, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Password changed successfully"
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