package com.rajveer.finance.portfolio.dto;

import com.rajveer.finance.common.enums.AssetType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PortfolioAssetResponse(
        Long id,
        String assetName,
        String symbol,
        AssetType assetType,
        BigDecimal quantity,
        BigDecimal averagePurchasePrice,
        BigDecimal currentPrice,
        BigDecimal investedAmount,
        BigDecimal currentValue,
        BigDecimal profitOrLoss,
        BigDecimal returnPercentage,
        LocalDate investmentDate,
        String institutionName,
        String accountReference,
        LocalDate maturityDate,
        BigDecimal interestRate,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}