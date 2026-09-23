package com.rajveer.finance.auth.mapper;

import com.rajveer.finance.auth.dto.AuthenticationResponse;
import com.rajveer.finance.auth.dto.RegisterRequest;
import com.rajveer.finance.common.enums.AccountStatus;
import com.rajveer.finance.common.enums.Role;
import com.rajveer.finance.user.entity.User;
import com.rajveer.finance.user.entity.UserProfile;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class RegistrationMapper {

    public User toUser(
            RegisterRequest request,
            String encodedPassword
    ) {
        User user = User.builder()
                .email(normalizeEmail(request.email()))
                .passwordHash(encodedPassword)
                .role(Role.USER)
                .accountStatus(AccountStatus.ACTIVE)
                .emailVerified(false)
                .build();

        UserProfile profile = UserProfile.builder()
                .firstName(request.firstName().trim())
                .lastName(request.lastName().trim())
                .phoneNumber(normalizePhoneNumber(request.phoneNumber()))
                .currencyCode("INR")
                .timezone("Asia/Kolkata")
                .build();

        user.attachProfile(profile);

        return user;
    }

    public AuthenticationResponse toRegistrationResponse(User user) {
        UserProfile profile = user.getProfile();

        return AuthenticationResponse.registrationResponse(
                user.getId(),
                profile.getFirstName(),
                profile.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getAccountStatus()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return null;
        }

        return phoneNumber.trim();
    }
}