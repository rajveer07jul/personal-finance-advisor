package com.rajveer.finance.portfolio.service;

import com.rajveer.finance.common.enums.AssetType;
import com.rajveer.finance.portfolio.dto.AssetAllocationResponse;
import com.rajveer.finance.portfolio.dto.PortfolioAssetRequest;
import com.rajveer.finance.portfolio.dto.PortfolioAssetResponse;
import com.rajveer.finance.portfolio.dto.PortfolioSummaryResponse;

import java.util.List;

public interface PortfolioService {

    PortfolioAssetResponse createAsset(
            Long userId,
            PortfolioAssetRequest request
    );

    List<PortfolioAssetResponse> getAssets(
            Long userId,
            AssetType assetType
    );

    PortfolioAssetResponse getAssetById(
            Long userId,
            Long assetId
    );

    PortfolioAssetResponse updateAsset(
            Long userId,
            Long assetId,
            PortfolioAssetRequest request
    );

    void deleteAsset(
            Long userId,
            Long assetId
    );

    PortfolioSummaryResponse getPortfolioSummary(
            Long userId
    );

    List<AssetAllocationResponse> getAssetAllocation(
            Long userId
    );
}
