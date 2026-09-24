package com.rajveer.finance.user.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UserProfileRequest(

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

        @Pattern(
                regexp = "^$|^[0-9+() -]{7,20}$",
                message = "Phone number format is invalid"
        )
        String phoneNumber,

        @Pattern(
                regexp = "^[A-Z]{3}$",
                message = "Currency code must contain exactly three uppercase letters"
        )
        String currencyCode,

        @DecimalMin(
                value = "0.00",
                inclusive = true,
                message = "Monthly income cannot be negative"
        )
        BigDecimal monthlyIncome,

        @NotBlank(message = "Timezone is required")
        @Size(
                max = 50,
                message = "Timezone must not exceed 50 characters"
        )
        String timezone
) {
}