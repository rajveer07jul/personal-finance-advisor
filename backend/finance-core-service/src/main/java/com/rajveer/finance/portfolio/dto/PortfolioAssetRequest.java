package com.rajveer.finance.portfolio.dto;

import com.rajveer.finance.common.enums.AssetType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PortfolioAssetRequest(

        @NotBlank(message = "Asset name is required")
        @Size(
                max = 150,
                message = "Asset name must not exceed 150 characters"
        )
        String assetName,

        @Size(
                max = 30,
                message = "Asset symbol must not exceed 30 characters"
        )
        String symbol,

        @NotNull(message = "Asset type is required")
        AssetType assetType,

        @NotNull(message = "Asset quantity is required")
        @DecimalMin(
                value = "0.000001",
                message = "Asset quantity must be greater than zero"
        )
        BigDecimal quantity,

        @NotNull(message = "Average purchase price is required")
        @DecimalMin(
                value = "0.00",
                inclusive = true,
                message = "Average purchase price cannot be negative"
        )
        BigDecimal averagePurchasePrice,

        @NotNull(message = "Current price is required")
        @DecimalMin(
                value = "0.00",
                inclusive = true,
                message = "Current price cannot be negative"
        )
        BigDecimal currentPrice,

        @NotNull(message = "Investment date is required")
        @PastOrPresent(
                message = "Investment date cannot be in the future"
        )
        LocalDate investmentDate,

        @Size(
                max = 150,
                message = "Institution name must not exceed 150 characters"
        )
        String institutionName,

        @Size(
                max = 100,
                message = "Account reference must not exceed 100 characters"
        )
        String accountReference,

        LocalDate maturityDate,

        @DecimalMin(
                value = "0.00",
                inclusive = true,
                message = "Interest rate cannot be negative"
        )
        @DecimalMax(
                value = "100.00",
                message = "Interest rate cannot exceed 100"
        )
        BigDecimal interestRate,

        @Size(
                max = 500,
                message = "Asset notes must not exceed 500 characters"
        )
        String notes
) {
}