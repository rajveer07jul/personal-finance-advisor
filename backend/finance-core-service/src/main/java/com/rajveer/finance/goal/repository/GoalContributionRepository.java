package com.rajveer.finance.goal.repository;

import com.rajveer.finance.goal.entity.GoalContribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoalContributionRepository
        extends JpaRepository<GoalContribution, Long> {

    Optional<GoalContribution>
    findByIdAndGoalIdAndGoalUserId(
            Long contributionId,
            Long goalId,
            Long userId
    );

    List<GoalContribution>
    findAllByGoalIdAndGoalUserIdOrderByContributionDateDescIdDesc(
            Long goalId,
            Long userId
    );

    long countByGoalIdAndGoalUserId(
            Long goalId,
            Long userId
    );
}