package com.rajveer.finance.portfolio.repository;

import com.rajveer.finance.common.enums.AssetType;
import com.rajveer.finance.portfolio.entity.PortfolioAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioAssetRepository
        extends JpaRepository<PortfolioAsset, Long> {

    Optional<PortfolioAsset> findByIdAndUserId(
            Long assetId,
            Long userId
    );

    List<PortfolioAsset> findAllByUserIdOrderByAssetTypeAscAssetNameAsc(
            Long userId
    );

    List<PortfolioAsset> findAllByUserIdAndAssetTypeOrderByAssetNameAsc(
            Long userId,
            AssetType assetType
    );

    boolean existsByIdAndUserId(
            Long assetId,
            Long userId
    );

    long countByUserId(Long userId);

    long countByUserIdAndAssetType(
            Long userId,
            AssetType assetType
    );
}