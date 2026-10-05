package com.rajveer.finance.portfolio.service;

import com.rajveer.finance.common.enums.AssetType;
import com.rajveer.finance.exception.BusinessRuleException;
import com.rajveer.finance.exception.ResourceNotFoundException;
import com.rajveer.finance.portfolio.dto.AssetAllocationResponse;
import com.rajveer.finance.portfolio.dto.PortfolioAssetRequest;
import com.rajveer.finance.portfolio.dto.PortfolioAssetResponse;
import com.rajveer.finance.portfolio.dto.PortfolioSummaryResponse;
import com.rajveer.finance.portfolio.entity.PortfolioAsset;
import com.rajveer.finance.portfolio.mapper.PortfolioMapper;
import com.rajveer.finance.portfolio.repository.PortfolioAssetRepository;
import com.rajveer.finance.user.entity.User;
import com.rajveer.finance.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl
        implements PortfolioService {

    private static final BigDecimal ONE_HUNDRED =
            new BigDecimal("100");

    private final PortfolioAssetRepository portfolioAssetRepository;
    private final UserRepository userRepository;
    private final PortfolioMapper portfolioMapper;

    @Override
    @Transactional
    public PortfolioAssetResponse createAsset(
            Long userId,
            PortfolioAssetRequest request
    ) {
        User user = findUserById(userId);

        validateBusinessRules(request);

        PortfolioAsset portfolioAsset =
                portfolioMapper.toEntity(
                        request,
                        user
                );

        PortfolioAsset savedAsset =
                portfolioAssetRepository.save(
                        portfolioAsset
                );

        return portfolioMapper.toResponse(
                savedAsset
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PortfolioAssetResponse> getAssets(
            Long userId,
            AssetType assetType
    ) {
        List<PortfolioAsset> portfolioAssets;

        if (assetType == null) {
            portfolioAssets =
                    portfolioAssetRepository
                            .findAllByUserIdOrderByAssetTypeAscAssetNameAsc(
                                    userId
                            );
        } else {
            portfolioAssets =
                    portfolioAssetRepository
                            .findAllByUserIdAndAssetTypeOrderByAssetNameAsc(
                                    userId,
                                    assetType
                            );
        }

        return portfolioAssets.stream()
                .map(portfolioMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioAssetResponse getAssetById(
            Long userId,
            Long assetId
    ) {
        PortfolioAsset portfolioAsset =
                findAssetOwnedByUser(
                        assetId,
                        userId
                );

        return portfolioMapper.toResponse(
                portfolioAsset
        );
    }

    @Override
    @Transactional
    public PortfolioAssetResponse updateAsset(
            Long userId,
            Long assetId,
            PortfolioAssetRequest request
    ) {
        PortfolioAsset portfolioAsset =
                findAssetOwnedByUser(
                        assetId,
                        userId
                );

        validateBusinessRules(request);

        portfolioMapper.updateEntity(
                portfolioAsset,
                request
        );

        PortfolioAsset savedAsset =
                portfolioAssetRepository.save(
                        portfolioAsset
                );

        return portfolioMapper.toResponse(
                savedAsset
        );
    }

    @Override
    @Transactional
    public void deleteAsset(
            Long userId,
            Long assetId
    ) {
        PortfolioAsset portfolioAsset =
                findAssetOwnedByUser(
                        assetId,
                        userId
                );

        portfolioAssetRepository.delete(
                portfolioAsset
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioSummaryResponse getPortfolioSummary(
            Long userId
    ) {
        List<PortfolioAsset> portfolioAssets =
                portfolioAssetRepository
                        .findAllByUserIdOrderByAssetTypeAscAssetNameAsc(
                                userId
                        );

        BigDecimal totalInvestedAmount =
                calculateTotalInvestedAmount(
                        portfolioAssets
                );

        BigDecimal totalCurrentValue =
                calculateTotalCurrentValue(
                        portfolioAssets
                );

        BigDecimal totalProfitOrLoss =
                totalCurrentValue.subtract(
                        totalInvestedAmount
                );

        BigDecimal overallReturnPercentage =
                calculatePercentage(
                        totalProfitOrLoss,
                        totalInvestedAmount
                );

        long profitableAssets =
                countProfitableAssets(
                        portfolioAssets
                );

        long lossMakingAssets =
                countLossMakingAssets(
                        portfolioAssets
                );

        List<AssetAllocationResponse> assetAllocations =
                calculateAssetAllocations(
                        portfolioAssets,
                        totalCurrentValue
                );

        return new PortfolioSummaryResponse(
                scaleMoney(totalInvestedAmount),
                scaleMoney(totalCurrentValue),
                scaleMoney(totalProfitOrLoss),
                overallReturnPercentage,
                portfolioAssets.size(),
                profitableAssets,
                lossMakingAssets,
                assetAllocations
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetAllocationResponse> getAssetAllocation(
            Long userId
    ) {
        List<PortfolioAsset> portfolioAssets =
                portfolioAssetRepository
                        .findAllByUserIdOrderByAssetTypeAscAssetNameAsc(
                                userId
                        );

        BigDecimal totalCurrentValue =
                calculateTotalCurrentValue(
                        portfolioAssets
                );

        return calculateAssetAllocations(
                portfolioAssets,
                totalCurrentValue
        );
    }

    private List<AssetAllocationResponse>
    calculateAssetAllocations(
            List<PortfolioAsset> portfolioAssets,
            BigDecimal totalCurrentValue
    ) {
        Map<AssetType, BigDecimal> investedAmountByType =
                new EnumMap<>(AssetType.class);

        Map<AssetType, BigDecimal> currentValueByType =
                new EnumMap<>(AssetType.class);

        Map<AssetType, Long> assetCountByType =
                new EnumMap<>(AssetType.class);

        for (PortfolioAsset portfolioAsset :
                portfolioAssets) {

            AssetType assetType =
                    portfolioAsset.getAssetType();

            BigDecimal investedAmount =
                    portfolioAsset
                            .calculateInvestedAmount();

            BigDecimal currentValue =
                    portfolioAsset
                            .calculateCurrentValue();

            investedAmountByType.merge(
                    assetType,
                    investedAmount,
                    BigDecimal::add
            );

            currentValueByType.merge(
                    assetType,
                    currentValue,
                    BigDecimal::add
            );

            assetCountByType.merge(
                    assetType,
                    1L,
                    Long::sum
            );
        }

        List<AssetAllocationResponse> allocations =
                new ArrayList<>();

        for (AssetType assetType :
                currentValueByType.keySet()) {

            BigDecimal investedAmount =
                    investedAmountByType.getOrDefault(
                            assetType,
                            BigDecimal.ZERO
                    );

            BigDecimal currentValue =
                    currentValueByType.getOrDefault(
                            assetType,
                            BigDecimal.ZERO
                    );

            BigDecimal allocationPercentage =
                    calculatePercentage(
                            currentValue,
                            totalCurrentValue
                    );

            long assetCount =
                    assetCountByType.getOrDefault(
                            assetType,
                            0L
                    );

            AssetAllocationResponse allocation =
                    new AssetAllocationResponse(
                            assetType,
                            scaleMoney(investedAmount),
                            scaleMoney(currentValue),
                            allocationPercentage,
                            assetCount
                    );

            allocations.add(allocation);
        }

        allocations.sort(
                Comparator.comparing(
                                AssetAllocationResponse
                                        ::allocationPercentage
                        )
                        .reversed()
        );

        return allocations;
    }

    private BigDecimal calculateTotalInvestedAmount(
            List<PortfolioAsset> portfolioAssets
    ) {
        return portfolioAssets.stream()
                .map(
                        PortfolioAsset
                                ::calculateInvestedAmount
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    private BigDecimal calculateTotalCurrentValue(
            List<PortfolioAsset> portfolioAssets
    ) {
        return portfolioAssets.stream()
                .map(
                        PortfolioAsset
                                ::calculateCurrentValue
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    private long countProfitableAssets(
            List<PortfolioAsset> portfolioAssets
    ) {
        return portfolioAssets.stream()
                .filter(portfolioAsset ->
                        portfolioAsset
                                .calculateProfitOrLoss()
                                .compareTo(
                                        BigDecimal.ZERO
                                ) > 0
                )
                .count();
    }

    private long countLossMakingAssets(
            List<PortfolioAsset> portfolioAssets
    ) {
        return portfolioAssets.stream()
                .filter(portfolioAsset ->
                        portfolioAsset
                                .calculateProfitOrLoss()
                                .compareTo(
                                        BigDecimal.ZERO
                                ) < 0
                )
                .count();
    }

    private BigDecimal calculatePercentage(
            BigDecimal numerator,
            BigDecimal denominator
    ) {
        if (denominator == null ||
                denominator.compareTo(
                        BigDecimal.ZERO
                ) == 0) {

            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }

        return numerator
                .multiply(ONE_HUNDRED)
                .divide(
                        denominator,
                        2,
                        RoundingMode.HALF_UP
                );
    }

    private BigDecimal scaleMoney(
            BigDecimal amount
    ) {
        if (amount == null) {
            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }

        return amount.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }

    private void validateBusinessRules(
            PortfolioAssetRequest request
    ) {
        validateMaturityDate(request);

        validateInterestRate(request);

        validateSymbol(request);
    }

    private void validateMaturityDate(
            PortfolioAssetRequest request
    ) {
        if (request.maturityDate() != null &&
                request.maturityDate().isBefore(
                        request.investmentDate()
                )) {

            throw new BusinessRuleException(
                    "Maturity date cannot be before investment date"
            );
        }

        if (requiresMaturityDate(
                request.assetType()
        ) && request.maturityDate() == null) {

            throw new BusinessRuleException(
                    "Maturity date is required for "
                            + formatAssetType(
                                    request.assetType()
                            )
            );
        }
    }

    private void validateInterestRate(
            PortfolioAssetRequest request
    ) {
        if (requiresInterestRate(
                request.assetType()
        ) && request.interestRate() == null) {

            throw new BusinessRuleException(
                    "Interest rate is required for "
                            + formatAssetType(
                                    request.assetType()
                            )
            );
        }
    }

    private void validateSymbol(
            PortfolioAssetRequest request
    ) {
        if (requiresSymbol(request.assetType()) &&
                (
                        request.symbol() == null ||
                                request.symbol().isBlank()
                )) {

            throw new BusinessRuleException(
                    "Asset symbol is required for "
                            + formatAssetType(
                                    request.assetType()
                            )
            );
        }
    }

    private boolean requiresMaturityDate(
            AssetType assetType
    ) {
        return assetType == AssetType.FIXED_DEPOSIT
                || assetType == AssetType.BOND;
    }

    private boolean requiresInterestRate(
            AssetType assetType
    ) {
        return assetType == AssetType.FIXED_DEPOSIT
                || assetType == AssetType.BOND;
    }

    private boolean requiresSymbol(
            AssetType assetType
    ) {
        return assetType == AssetType.STOCK
                || assetType == AssetType.MUTUAL_FUND;
    }

    private String formatAssetType(
            AssetType assetType
    ) {
        return assetType.name()
                .replace('_', ' ');
    }

    private PortfolioAsset findAssetOwnedByUser(
            Long assetId,
            Long userId
    ) {
        return portfolioAssetRepository
                .findByIdAndUserId(
                        assetId,
                        userId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Portfolio asset",
                                "id",
                                assetId
                        )
                );
    }

    private User findUserById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User",
                                "id",
                                userId
                        )
                );
    }
}