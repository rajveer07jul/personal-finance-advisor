package com.rajveer.finance.goal.service;

import com.rajveer.finance.common.enums.GoalStatus;
import com.rajveer.finance.common.enums.GoalType;
import com.rajveer.finance.goal.dto.GoalContributionRequest;
import com.rajveer.finance.goal.dto.GoalContributionResponse;
import com.rajveer.finance.goal.dto.GoalProgressResponse;
import com.rajveer.finance.goal.dto.GoalRequest;
import com.rajveer.finance.goal.dto.GoalResponse;

import java.util.List;

public interface GoalService {

    GoalResponse createGoal(
            Long userId,
            GoalRequest request
    );

    List<GoalResponse> getGoals(
            Long userId,
            GoalStatus status,
            GoalType goalType
    );

    GoalResponse getGoalById(
            Long userId,
            Long goalId
    );

    GoalResponse updateGoal(
            Long userId,
            Long goalId,
            GoalRequest request
    );

    void deleteGoal(
            Long userId,
            Long goalId
    );

    GoalContributionResponse addContribution(
            Long userId,
            Long goalId,
            GoalContributionRequest request
    );

    List<GoalContributionResponse> getContributions(
            Long userId,
            Long goalId
    );

    void deleteContribution(
            Long userId,
            Long goalId,
            Long contributionId
    );

    GoalProgressResponse getGoalProgress(
            Long userId,
            Long goalId
    );

    GoalResponse pauseGoal(
            Long userId,
            Long goalId
    );

    GoalResponse resumeGoal(
            Long userId,
            Long goalId
    );

    GoalResponse cancelGoal(
            Long userId,
            Long goalId
    );
}