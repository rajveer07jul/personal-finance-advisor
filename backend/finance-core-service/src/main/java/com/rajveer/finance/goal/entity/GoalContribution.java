package com.rajveer.finance.goal.entity;

import com.rajveer.finance.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "goal_contributions",
        indexes = {
                @Index(
                        name = "idx_goal_contributions_goal_id",
                        columnList = "goal_id"
                ),
                @Index(
                        name = "idx_goal_contributions_goal_date",
                        columnList = "goal_id, contribution_date"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalContribution extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "goal_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_goal_contributions_goal"
            )
    )
    private FinancialGoal goal;

    @Column(
            name = "amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    @Column(
            name = "contribution_date",
            nullable = false
    )
    private LocalDate contributionDate;

    @Column(
            name = "note",
            length = 300
    )
    private String note;
}