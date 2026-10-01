package com.rajveer.finance.goal.entity;

import com.rajveer.finance.common.entity.AuditableEntity;
import com.rajveer.finance.common.enums.GoalPriority;
import com.rajveer.finance.common.enums.GoalStatus;
import com.rajveer.finance.common.enums.GoalType;
import com.rajveer.finance.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "financial_goals",
        indexes = {
                @Index(
                        name = "idx_financial_goals_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_financial_goals_user_status",
                        columnList = "user_id, status"
                ),
                @Index(
                        name = "idx_financial_goals_target_date",
                        columnList = "target_date"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialGoal extends AuditableEntity {

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
                    name = "fk_financial_goals_user"
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
            name = "description",
            length = 500
    )
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "goal_type",
            nullable = false,
            length = 30
    )
    private GoalType goalType;

    @Column(
            name = "target_amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal targetAmount;

    @Column(
            name = "current_amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    @Builder.Default
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Column(
            name = "target_date",
            nullable = false
    )
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "priority",
            nullable = false,
            length = 20
    )
    @Builder.Default
    private GoalPriority priority = GoalPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    @Builder.Default
    private GoalStatus status = GoalStatus.NOT_STARTED;

    @Column(
            name = "notes",
            length = 500
    )
    private String notes;

    @OneToMany(
            mappedBy = "goal",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("contributionDate DESC, id DESC")
    @Builder.Default
    private List<GoalContribution> contributions =
            new ArrayList<>();

    public void addContribution(
            GoalContribution contribution
    ) {
        contributions.add(contribution);
        contribution.setGoal(this);
    }

    public void removeContribution(
            GoalContribution contribution
    ) {
        contributions.remove(contribution);
        contribution.setGoal(null);
    }

    public boolean isCompleted() {
        return status == GoalStatus.COMPLETED;
    }

    public boolean isCancelled() {
        return status == GoalStatus.CANCELLED;
    }

    public boolean isPaused() {
        return status == GoalStatus.PAUSED;
    }

    public boolean acceptsContributions() {
        return !isCompleted()
                && !isCancelled()
                && !isPaused();
    }

    public void addToCurrentAmount(
            BigDecimal contributionAmount
    ) {
        currentAmount = currentAmount.add(
                contributionAmount
        );

        updateStatusAfterContribution();
    }

    public void subtractFromCurrentAmount(
            BigDecimal contributionAmount
    ) {
        currentAmount = currentAmount.subtract(
                contributionAmount
        );

        if (currentAmount.compareTo(BigDecimal.ZERO) < 0) {
            currentAmount = BigDecimal.ZERO;
        }

        updateStatusAfterContribution();
    }

    public void updateStatusAfterContribution() {
        if (currentAmount.compareTo(targetAmount) >= 0) {
            status = GoalStatus.COMPLETED;
            return;
        }

        if (currentAmount.compareTo(BigDecimal.ZERO) > 0) {
            status = GoalStatus.IN_PROGRESS;
            return;
        }

        status = GoalStatus.NOT_STARTED;
    }
}