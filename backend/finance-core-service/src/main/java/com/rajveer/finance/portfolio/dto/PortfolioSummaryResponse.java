package com.rajveer.finance.portfolio.dto;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioSummaryResponse(
        BigDecimal totalInvestedAmount,
        BigDecimal totalCurrentValue,
        BigDecimal totalProfitOrLoss,
        BigDecimal overallReturnPercentage,
        long totalAssets,
        long profitableAssets,
        long lossMakingAssets,
        List<AssetAllocationResponse> assetAllocations
) {
}