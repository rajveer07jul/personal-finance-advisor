package com.rajveer.finance.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "First name is required")
        @Size(
                min = 2,
                max = 50,
                message = "First name must contain between 2 and 50 characters"
        )
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(
                min = 2,
                max = 50,
                message = "Last name must contain between 2 and 50 characters"
        )
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "A valid email address is required")
        @Size(
                max = 150,
                message = "Email must not exceed 150 characters"
        )
        String email,

        @NotBlank(message = "Password is required")
        @Size(
                min = 8,
                max = 72,
                message = "Password must contain between 8 and 72 characters"
        )
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+$",
                message = "Password must contain uppercase, lowercase, number and special character"
        )
        String password,

        @Pattern(
                regexp = "^$|^[0-9+() -]{7,20}$",
                message = "Phone number format is invalid"
        )
        String phoneNumber
) {
}