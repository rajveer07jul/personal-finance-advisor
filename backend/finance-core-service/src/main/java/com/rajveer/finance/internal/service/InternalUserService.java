package com.rajveer.finance.internal.service;

import com.rajveer.finance.common.enums.AccountStatus;
import com.rajveer.finance.common.enums.Role;
import com.rajveer.finance.common.response.PagedResponse;
import com.rajveer.finance.internal.dto.InternalPlatformSummaryResponse;
import com.rajveer.finance.internal.dto.InternalUserResponse;
import com.rajveer.finance.internal.dto.InternalUserStatusRequest;
import org.springframework.data.domain.Pageable;

public interface InternalUserService {

    PagedResponse<InternalUserResponse> getUsers(
            String search,
            AccountStatus accountStatus,
            Role role,
            Pageable pageable
    );

    InternalUserResponse getUserById(
            Long userId
    );

    InternalUserResponse updateUserStatus(
            Long userId,
            InternalUserStatusRequest request
    );

    InternalPlatformSummaryResponse
    getPlatformSummary();
}