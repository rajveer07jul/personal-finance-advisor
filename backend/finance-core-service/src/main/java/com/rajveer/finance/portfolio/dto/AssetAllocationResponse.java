package com.rajveer.finance.portfolio.dto;

import com.rajveer.finance.common.enums.AssetType;

import java.math.BigDecimal;

public record AssetAllocationResponse(
        AssetType assetType,
        BigDecimal investedAmount,
        BigDecimal currentValue,
        BigDecimal allocationPercentage,
        long assetCount
) {
}