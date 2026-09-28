package com.rajveer.finance.budget.entity;

import com.rajveer.finance.common.entity.AuditableEntity;
import com.rajveer.finance.common.enums.BudgetPeriod;
import com.rajveer.finance.common.enums.ExpenseCategory;
import com.rajveer.finance.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "budgets",
        indexes = {
                @Index(
                        name = "idx_budgets_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_budgets_user_month",
                        columnList = "user_id, budget_month"
                ),
                @Index(
                        name = "idx_budgets_user_category_month",
                        columnList = "user_id, category, budget_month"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget extends AuditableEntity {

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
                    name = "fk_budgets_user"
            )
    )
    private User user;

    @Column(
            name = "name",
            nullable = false,
            length = 100
    )
    private String name;

    @Column(
            name = "amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "period",
            nullable = false,
            length = 20
    )
    @Builder.Default
    private BudgetPeriod period = BudgetPeriod.MONTHLY;

    @Column(
            name = "budget_month",
            nullable = false
    )
    private LocalDate budgetMonth;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "category",
            length = 40
    )
    private ExpenseCategory category;

    @Column(
            name = "alert_threshold",
            nullable = false,
            precision = 5,
            scale = 2
    )
    @Builder.Default
    private BigDecimal alertThreshold =
            new BigDecimal("80.00");

    @Column(
            name = "notes",
            length = 500
    )
    private String notes;

    public boolean isOverallBudget() {
        return category == null;
    }

    public boolean isCategoryBudget() {
        return category != null;
    }
}