package com.rajveer.finance.user.mapper;

import com.rajveer.finance.user.dto.UserProfileRequest;
import com.rajveer.finance.user.dto.UserProfileResponse;
import com.rajveer.finance.user.entity.User;
import com.rajveer.finance.user.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserProfileResponse toProfileResponse(User user) {
        UserProfile profile = user.getProfile();

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getAccountStatus(),
                user.isEmailVerified(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getPhoneNumber(),
                profile.getCurrencyCode(),
                profile.getMonthlyIncome(),
                profile.getTimezone(),
                user.getLastLoginAt(),
                user.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }

    public void updateProfile(
            UserProfile profile,
            UserProfileRequest request
    ) {
        profile.setFirstName(request.firstName().trim());
        profile.setLastName(request.lastName().trim());
        profile.setPhoneNumber(
                normalizeOptionalValue(request.phoneNumber())
        );
        profile.setCurrencyCode(
                request.currencyCode().trim().toUpperCase()
        );
        profile.setMonthlyIncome(request.monthlyIncome());
        profile.setTimezone(request.timezone().trim());
    }

    private String normalizeOptionalValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}