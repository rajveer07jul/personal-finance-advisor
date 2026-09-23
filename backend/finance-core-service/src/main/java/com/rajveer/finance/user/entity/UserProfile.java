package com.rajveer.finance.user.entity;

import com.rajveer.finance.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "user_profiles",
        indexes = {
                @Index(
                        name = "idx_user_profiles_user_id",
                        columnList = "user_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(
                    name = "fk_user_profiles_user"
            )
    )
    private User user;

    @Column(
            name = "first_name",
            nullable = false,
            length = 50
    )
    private String firstName;

    @Column(
            name = "last_name",
            nullable = false,
            length = 50
    )
    private String lastName;

    @Column(
            name = "phone_number",
            length = 20
    )
    private String phoneNumber;

    @Column(
            name = "currency_code",
            nullable = false,
            length = 3
    )
    @Builder.Default
    private String currencyCode = "INR";

    @Column(
            name = "monthly_income",
            precision = 19,
            scale = 2
    )
    private BigDecimal monthlyIncome;

    @Column(
            name = "timezone",
            nullable = false,
            length = 50
    )
    @Builder.Default
    private String timezone = "Asia/Kolkata";

    public String getFullName() {
        return firstName + " " + lastName;
    }
}