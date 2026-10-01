package com.rajveer.finance.goal.repository;

import com.rajveer.finance.common.enums.GoalStatus;
import com.rajveer.finance.common.enums.GoalType;
import com.rajveer.finance.goal.entity.FinancialGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FinancialGoalRepository
        extends JpaRepository<FinancialGoal, Long> {

    Optional<FinancialGoal> findByIdAndUserId(
            Long goalId,
            Long userId
    );

    List<FinancialGoal> findAllByUserIdOrderByPriorityDescTargetDateAsc(
            Long userId
    );

    List<FinancialGoal> findAllByUserIdAndStatusOrderByTargetDateAsc(
            Long userId,
            GoalStatus status
    );

    List<FinancialGoal> findAllByUserIdAndGoalTypeOrderByTargetDateAsc(
            Long userId,
            GoalType goalType
    );

    List<FinancialGoal> findAllByUserIdAndTargetDateBeforeAndStatusNot(
            Long userId,
            LocalDate targetDate,
            GoalStatus excludedStatus
    );

    long countByUserId(Long userId);

    long countByUserIdAndStatus(
            Long userId,
            GoalStatus status
    );
}