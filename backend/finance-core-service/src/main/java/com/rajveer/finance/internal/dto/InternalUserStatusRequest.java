package com.rajveer.finance.internal.dto;

import com.rajveer.finance.common.enums.AccountStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InternalUserStatusRequest(

        @NotNull(message = "Account status is required")
        AccountStatus accountStatus,

        @Size(
                max = 300,
                message = "Status-change reason must not exceed 300 characters"
        )
        String reason
) {
}
