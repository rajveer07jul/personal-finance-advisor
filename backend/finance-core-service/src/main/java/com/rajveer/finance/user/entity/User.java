package com.rajveer.finance.user.entity;

import com.rajveer.finance.common.entity.AuditableEntity;
import com.rajveer.finance.common.enums.AccountStatus;
import com.rajveer.finance.common.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_email",
                        columnNames = "email"
                )
        },
        indexes = {
                @Index(
                        name = "idx_users_email",
                        columnList = "email"
                ),
                @Index(
                        name = "idx_users_account_status",
                        columnList = "account_status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "email",
            nullable = false,
            length = 150
    )
    private String email;

    @Column(
            name = "password_hash",
            nullable = false,
            length = 255
    )
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "role",
            nullable = false,
            length = 20
    )
    @Builder.Default
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "account_status",
            nullable = false,
            length = 20
    )
    @Builder.Default
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @Column(
            name = "email_verified",
            nullable = false
    )
    @Builder.Default
    private boolean emailVerified = false;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private UserProfile profile;

    public void attachProfile(UserProfile profile) {
        this.profile = profile;

        if (profile != null) {
            profile.setUser(this);
        }
    }

    public boolean isActive() {
        return accountStatus == AccountStatus.ACTIVE;
    }

    public void recordSuccessfulLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }
}