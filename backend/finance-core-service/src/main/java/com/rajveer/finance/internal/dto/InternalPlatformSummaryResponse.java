package com.rajveer.finance.internal.dto;

public record InternalPlatformSummaryResponse(
        long totalUsers,
        long activeUsers,
        long inactiveUsers,
        long suspendedUsers,
        long lockedUsers,
        long adminUsers,
        long regularUsers,
        long totalExpenses,
        long totalBudgets,
        long totalGoals,
        long completedGoals,
        long totalPortfolioAssets
) {
}
