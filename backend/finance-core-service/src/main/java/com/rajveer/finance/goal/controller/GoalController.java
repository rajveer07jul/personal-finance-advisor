package com.rajveer.finance.goal.controller;

import com.rajveer.finance.common.enums.GoalStatus;
import com.rajveer.finance.common.enums.GoalType;
import com.rajveer.finance.common.response.ApiResponse;
import com.rajveer.finance.goal.dto.GoalContributionRequest;
import com.rajveer.finance.goal.dto.GoalContributionResponse;
import com.rajveer.finance.goal.dto.GoalProgressResponse;
import com.rajveer.finance.goal.dto.GoalRequest;
import com.rajveer.finance.goal.dto.GoalResponse;
import com.rajveer.finance.goal.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<ApiResponse<GoalResponse>>
    createGoal(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody GoalRequest request
    ) {
        GoalResponse response =
                goalService.createGoal(
                        extractUserId(jwt),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Financial goal created successfully",
                                response
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GoalResponse>>>
    getGoals(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false)
            GoalStatus status,
            @RequestParam(required = false)
            GoalType goalType
    ) {
        List<GoalResponse> response =
                goalService.getGoals(
                        extractUserId(jwt),
                        status,
                        goalType
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Financial goals retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<ApiResponse<GoalResponse>>
    getGoalById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long goalId
    ) {
        GoalResponse response =
                goalService.getGoalById(
                        extractUserId(jwt),
                        goalId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Financial goal retrieved successfully",
                        response
                )
        );
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<ApiResponse<GoalResponse>>
    updateGoal(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long goalId,
            @Valid @RequestBody GoalRequest request
    ) {
        GoalResponse response =
                goalService.updateGoal(
                        extractUserId(jwt),
                        goalId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Financial goal updated successfully",
                        response
                )
        );
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<ApiResponse<Void>>
    deleteGoal(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long goalId
    ) {
        goalService.deleteGoal(
                extractUserId(jwt),
                goalId
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Financial goal deleted successfully"
                )
        );
    }

    @PostMapping("/{goalId}/contributions")
    public ResponseEntity<ApiResponse<GoalContributionResponse>>
    addContribution(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long goalId,
            @Valid @RequestBody
            GoalContributionRequest request
    ) {
        GoalContributionResponse response =
                goalService.addContribution(
                        extractUserId(jwt),
                        goalId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Goal contribution added successfully",
                                response
                        )
                );
    }

    @GetMapping("/{goalId}/contributions")
    public ResponseEntity<ApiResponse<List<GoalContributionResponse>>>
    getContributions(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long goalId
    ) {
        List<GoalContributionResponse> response =
                goalService.getContributions(
                        extractUserId(jwt),
                        goalId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Goal contributions retrieved successfully",
                        response
                )
        );
    }

    @DeleteMapping(
            "/{goalId}/contributions/{contributionId}"
    )
    public ResponseEntity<ApiResponse<Void>>
    deleteContribution(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long goalId,
            @PathVariable Long contributionId
    ) {
        goalService.deleteContribution(
                extractUserId(jwt),
                goalId,
                contributionId
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Goal contribution deleted successfully"
                )
        );
    }

    @GetMapping("/{goalId}/progress")
    public ResponseEntity<ApiResponse<GoalProgressResponse>>
    getGoalProgress(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long goalId
    ) {
        GoalProgressResponse response =
                goalService.getGoalProgress(
                        extractUserId(jwt),
                        goalId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Goal progress retrieved successfully",
                        response
                )
        );
    }

    @PatchMapping("/{goalId}/pause")
    public ResponseEntity<ApiResponse<GoalResponse>>
    pauseGoal(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long goalId
    ) {
        GoalResponse response =
                goalService.pauseGoal(
                        extractUserId(jwt),
                        goalId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Financial goal paused successfully",
                        response
                )
        );
    }

    @PatchMapping("/{goalId}/resume")
    public ResponseEntity<ApiResponse<GoalResponse>>
    resumeGoal(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long goalId
    ) {
        GoalResponse response =
                goalService.resumeGoal(
                        extractUserId(jwt),
                        goalId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Financial goal resumed successfully",
                        response
                )
        );
    }

    @PatchMapping("/{goalId}/cancel")
    public ResponseEntity<ApiResponse<GoalResponse>>
    cancelGoal(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long goalId
    ) {
        GoalResponse response =
                goalService.cancelGoal(
                        extractUserId(jwt),
                        goalId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Financial goal cancelled successfully",
                        response
                )
        );
    }

    private Long extractUserId(Jwt jwt) {
        Number userId = jwt.getClaim("userId");

        if (userId == null) {
            throw new IllegalStateException(
                    "Authenticated token does not contain a user ID"
            );
        }

        return userId.longValue();
    }
}