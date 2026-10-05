package com.rajveer.finance.portfolio.entity;

import com.rajveer.finance.common.entity.AuditableEntity;
import com.rajveer.finance.common.enums.AssetType;
import com.rajveer.finance.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "portfolio_assets",
        indexes = {
                @Index(
                        name = "idx_portfolio_assets_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_portfolio_assets_user_type",
                        columnList = "user_id, asset_type"
                ),
                @Index(
                        name = "idx_portfolio_assets_investment_date",
                        columnList = "investment_date"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioAsset extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_portfolio_assets_user"
            )
    )
    private User user;

    @Column(
            name = "asset_name",
            nullable = false,
            length = 150
    )
    private String assetName;

    @Column(
            name = "symbol",
            length = 30
    )
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "asset_type",
            nullable = false,
            length = 30
    )
    private AssetType assetType;

    @Column(
            name = "quantity",
            nullable = false,
            precision = 19,
            scale = 6
    )
    private BigDecimal quantity;

    @Column(
            name = "average_purchase_price",
            nullable = false,
            precision = 19,
            scale = 4
    )
    private BigDecimal averagePurchasePrice;

    @Column(
            name = "current_price",
            nullable = false,
            precision = 19,
            scale = 4
    )
    private BigDecimal currentPrice;

    @Column(
            name = "investment_date",
            nullable = false
    )
    private LocalDate investmentDate;

    @Column(
            name = "institution_name",
            length = 150
    )
    private String institutionName;

    @Column(
            name = "account_reference",
            length = 100
    )
    private String accountReference;

    @Column(
            name = "maturity_date"
    )
    private LocalDate maturityDate;

    @Column(
            name = "interest_rate",
            precision = 7,
            scale = 4
    )
    private BigDecimal interestRate;

    @Column(
            name = "notes",
            length = 500
    )
    private String notes;

    public BigDecimal calculateInvestedAmount() {
        return quantity.multiply(
                averagePurchasePrice
        );
    }

    public BigDecimal calculateCurrentValue() {
        return quantity.multiply(
                currentPrice
        );
    }

    public BigDecimal calculateProfitOrLoss() {
        return calculateCurrentValue()
                .subtract(calculateInvestedAmount());
    }
}