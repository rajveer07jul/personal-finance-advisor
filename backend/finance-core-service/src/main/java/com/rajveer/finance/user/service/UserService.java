package com.rajveer.finance.user.service;

import com.rajveer.finance.user.dto.ChangePasswordRequest;
import com.rajveer.finance.user.dto.UserProfileRequest;
import com.rajveer.finance.user.dto.UserProfileResponse;

public interface UserService {

    UserProfileResponse getCurrentUserProfile(Long userId);

    UserProfileResponse updateCurrentUserProfile(
            Long userId,
            UserProfileRequest request
    );

    void changePassword(
            Long userId,
            ChangePasswordRequest request
    );
}