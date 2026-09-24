package com.rajveer.finance.user.service;

import com.rajveer.finance.exception.BusinessRuleException;
import com.rajveer.finance.exception.ResourceNotFoundException;
import com.rajveer.finance.user.dto.ChangePasswordRequest;
import com.rajveer.finance.user.dto.UserProfileRequest;
import com.rajveer.finance.user.dto.UserProfileResponse;
import com.rajveer.finance.user.entity.User;
import com.rajveer.finance.user.entity.UserProfile;
import com.rajveer.finance.user.mapper.UserMapper;
import com.rajveer.finance.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile(Long userId) {
        User user = findUserById(userId);

        return userMapper.toProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateCurrentUserProfile(
            Long userId,
            UserProfileRequest request
    ) {
        User user = findUserById(userId);

        UserProfile profile = user.getProfile();

        if (profile == null) {
            throw new ResourceNotFoundException(
                    "User profile",
                    "userId",
                    userId
            );
        }

        userMapper.updateProfile(profile, request);

        User savedUser = userRepository.save(user);

        return userMapper.toProfileResponse(savedUser);
    }

    @Override
    @Transactional
    public void changePassword(
            Long userId,
            ChangePasswordRequest request
    ) {
        User user = findUserById(userId);

        if (!passwordEncoder.matches(
                request.currentPassword(),
                user.getPasswordHash()
        )) {
            throw new BusinessRuleException(
                    "Current password is incorrect"
            );
        }

        if (!request.newPassword().equals(
                request.confirmNewPassword()
        )) {
            throw new BusinessRuleException(
                    "New password and confirmation do not match"
            );
        }

        if (passwordEncoder.matches(
                request.newPassword(),
                user.getPasswordHash()
        )) {
            throw new BusinessRuleException(
                    "New password must be different from the current password"
            );
        }

        user.setPasswordHash(
                passwordEncoder.encode(request.newPassword())
        );

        userRepository.save(user);
    }

    private User findUserById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User",
                                "id",
                                userId
                        )
                );
    }
}