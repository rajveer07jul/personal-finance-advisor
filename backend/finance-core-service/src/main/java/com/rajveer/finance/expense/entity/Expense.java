package com.rajveer.finance.expense.entity;

import com.rajveer.finance.common.entity.AuditableEntity;
import com.rajveer.finance.common.enums.ExpenseCategory;
import com.rajveer.finance.common.enums.PaymentMethod;
import com.rajveer.finance.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "expenses",
        indexes = {
                @Index(
                        name = "idx_expenses_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_expenses_user_date",
                        columnList = "user_id, expense_date"
                ),
                @Index(
                        name = "idx_expenses_user_category",
                        columnList = "user_id, category"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense extends AuditableEntity {

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
                    name = "fk_expenses_user"
            )
    )
    private User user;

    @Column(
            name = "title",
            nullable = false,
            length = 100
    )
    private String title;

    @Column(
            name = "description",
            length = 500
    )
    private String description;

    @Column(
            name = "amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "category",
            nullable = false,
            length = 40
    )
    private ExpenseCategory category;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_method",
            nullable = false,
            length = 30
    )
    private PaymentMethod paymentMethod;

    @Column(
            name = "expense_date",
            nullable = false
    )
    private LocalDate expenseDate;

    @Column(
            name = "merchant_name",
            length = 100
    )
    private String merchantName;

    @Column(
            name = "notes",
            length = 1000
    )
    private String notes;
}
