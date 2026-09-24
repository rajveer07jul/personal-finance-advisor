package com.rajveer.finance.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(

        @NotBlank(message = "Current password is required")
        String currentPassword,

        @NotBlank(message = "New password is required")
        @Size(
                min = 8,
                max = 72,
                message = "New password must contain between 8 and 72 characters"
        )
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+$",
                message = "New password must contain uppercase, lowercase, number and special character"
        )
        String newPassword,

        @NotBlank(message = "Password confirmation is required")
        String confirmNewPassword
) {
}