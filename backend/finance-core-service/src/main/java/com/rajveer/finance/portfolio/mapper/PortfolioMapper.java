package com.rajveer.finance.portfolio.mapper;

import com.rajveer.finance.portfolio.dto.PortfolioAssetRequest;
import com.rajveer.finance.portfolio.dto.PortfolioAssetResponse;
import com.rajveer.finance.portfolio.entity.PortfolioAsset;
import com.rajveer.finance.user.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

@Component
public class PortfolioMapper {

    private static final BigDecimal ONE_HUNDRED =
            new BigDecimal("100");

    public PortfolioAsset toEntity(
            PortfolioAssetRequest request,
            User user
    ) {
        return PortfolioAsset.builder()
                .user(user)
                .assetName(request.assetName().trim())
                .symbol(normalizeSymbol(request.symbol()))
                .assetType(request.assetType())
                .quantity(request.quantity())
                .averagePurchasePrice(
                        request.averagePurchasePrice()
                )
                .currentPrice(request.currentPrice())
                .investmentDate(request.investmentDate())
                .institutionName(
                        normalizeOptionalValue(
                                request.institutionName()
                        )
                )
                .accountReference(
                        normalizeOptionalValue(
                                request.accountReference()
                        )
                )
                .maturityDate(request.maturityDate())
                .interestRate(request.interestRate())
                .notes(
                        normalizeOptionalValue(request.notes())
                )
                .build();
    }

    public void updateEntity(
            PortfolioAsset asset,
            PortfolioAssetRequest request
    ) {
        asset.setAssetName(request.assetName().trim());
        asset.setSymbol(normalizeSymbol(request.symbol()));
        asset.setAssetType(request.assetType());
        asset.setQuantity(request.quantity());
        asset.setAveragePurchasePrice(
                request.averagePurchasePrice()
        );
        asset.setCurrentPrice(request.currentPrice());
        asset.setInvestmentDate(request.investmentDate());
        asset.setInstitutionName(
                normalizeOptionalValue(
                        request.institutionName()
                )
        );
        asset.setAccountReference(
                normalizeOptionalValue(
                        request.accountReference()
                )
        );
        asset.setMaturityDate(request.maturityDate());
        asset.setInterestRate(request.interestRate());
        asset.setNotes(
                normalizeOptionalValue(request.notes())
        );
    }

    public PortfolioAssetResponse toResponse(
            PortfolioAsset asset
    ) {
        BigDecimal investedAmount =
                asset.calculateInvestedAmount()
                        .setScale(2, RoundingMode.HALF_UP);

        BigDecimal currentValue =
                asset.calculateCurrentValue()
                        .setScale(2, RoundingMode.HALF_UP);

        BigDecimal profitOrLoss =
                currentValue.subtract(investedAmount)
                        .setScale(2, RoundingMode.HALF_UP);

        BigDecimal returnPercentage =
                calculateReturnPercentage(
                        profitOrLoss,
                        investedAmount
                );

        return new PortfolioAssetResponse(
                asset.getId(),
                asset.getAssetName(),
                asset.getSymbol(),
                asset.getAssetType(),
                asset.getQuantity(),
                asset.getAveragePurchasePrice(),
                asset.getCurrentPrice(),
                investedAmount,
                currentValue,
                profitOrLoss,
                returnPercentage,
                asset.getInvestmentDate(),
                asset.getInstitutionName(),
                asset.getAccountReference(),
                asset.getMaturityDate(),
                asset.getInterestRate(),
                asset.getNotes(),
                asset.getCreatedAt(),
                asset.getUpdatedAt()
        );
    }

    private BigDecimal calculateReturnPercentage(
            BigDecimal profitOrLoss,
            BigDecimal investedAmount
    ) {
        if (investedAmount.compareTo(
                BigDecimal.ZERO
        ) == 0) {
            return BigDecimal.ZERO
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return profitOrLoss
                .multiply(ONE_HUNDRED)
                .divide(
                        investedAmount,
                        2,
                        RoundingMode.HALF_UP
                );
    }

    private String normalizeSymbol(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            return null;
        }

        return symbol
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    private String normalizeOptionalValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}