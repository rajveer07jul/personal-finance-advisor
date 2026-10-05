package com.rajveer.finance.internal.controller;

import com.rajveer.finance.common.enums.AccountStatus;
import com.rajveer.finance.common.enums.Role;
import com.rajveer.finance.common.response.ApiResponse;
import com.rajveer.finance.common.response.PagedResponse;
import com.rajveer.finance.internal.dto.InternalPlatformSummaryResponse;
import com.rajveer.finance.internal.dto.InternalUserResponse;
import com.rajveer.finance.internal.dto.InternalUserStatusRequest;
import com.rajveer.finance.internal.service.InternalUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class InternalUserController {

    private static final Set<String>
            ALLOWED_SORT_FIELDS =
            Set.of(
                    "id",
                    "email",
                    "role",
                    "accountStatus",
                    "createdAt",
                    "lastLoginAt"
            );

    private final InternalUserService
            internalUserService;

    @GetMapping("/users")
    public ResponseEntity<
            ApiResponse<
                    PagedResponse<InternalUserResponse>
                    >
            > getUsers(
            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            AccountStatus accountStatus,

            @RequestParam(required = false)
            Role role,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "desc")
            String sortDirection
    ) {
        validatePagination(page, size);
        validateSortField(sortBy);

        Sort.Direction direction =
                "asc".equalsIgnoreCase(sortDirection)
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortBy)
        );

        PagedResponse<InternalUserResponse> response =
                internalUserService.getUsers(
                        search,
                        accountStatus,
                        role,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Users retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<
            ApiResponse<InternalUserResponse>
            > getUserById(
            @PathVariable Long userId
    ) {
        InternalUserResponse response =
                internalUserService
                        .getUserById(userId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User retrieved successfully",
                        response
                )
        );
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<
            ApiResponse<InternalUserResponse>
            > updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody
            InternalUserStatusRequest request
    ) {
        InternalUserResponse response =
                internalUserService
                        .updateUserStatus(
                                userId,
                                request
                        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User account status updated successfully",
                        response
                )
        );
    }

    @GetMapping("/reports/platform-summary")
    public ResponseEntity<
            ApiResponse<
                    InternalPlatformSummaryResponse
                    >
            > getPlatformSummary() {
        InternalPlatformSummaryResponse response =
                internalUserService
                        .getPlatformSummary();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Platform summary retrieved successfully",
                        response
                )
        );
    }

    private void validatePagination(
            int page,
            int size
    ) {
        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100"
            );
        }
    }

    private void validateSortField(
            String sortBy
    ) {
        if (!ALLOWED_SORT_FIELDS.contains(
                sortBy
        )) {
            throw new IllegalArgumentException(
                    "Unsupported user sort field: "
                            + sortBy
            );
        }
    }
}