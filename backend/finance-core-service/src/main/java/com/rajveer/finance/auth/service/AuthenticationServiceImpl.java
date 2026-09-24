package com.rajveer.finance.auth.service;

import com.rajveer.finance.auth.dto.AuthenticationResponse;
import com.rajveer.finance.auth.dto.LoginRequest;
import com.rajveer.finance.auth.dto.RegisterRequest;
import com.rajveer.finance.auth.mapper.RegistrationMapper;
import com.rajveer.finance.exception.DuplicateResourceException;
import com.rajveer.finance.exception.ResourceNotFoundException;
import com.rajveer.finance.security.jwt.JwtService;
import com.rajveer.finance.user.entity.User;
import com.rajveer.finance.user.entity.UserProfile;
import com.rajveer.finance.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl
        implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegistrationMapper registrationMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthenticationResponse register(
            RegisterRequest request
    ) {
        String normalizedEmail =
                normalizeEmail(request.email());

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new DuplicateResourceException(
                    "User",
                    "email",
                    normalizedEmail
            );
        }

        String encodedPassword =
                passwordEncoder.encode(request.password());

        User user = registrationMapper.toUser(
                request,
                encodedPassword
        );

        User savedUser = userRepository.save(user);

        return registrationMapper
                .toRegistrationResponse(savedUser);
    }

    @Override
    @Transactional
    public AuthenticationResponse login(
            LoginRequest request
    ) {
        String normalizedEmail =
                normalizeEmail(request.email());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        normalizedEmail,
                        request.password()
                )
        );

        User user = userRepository
                .findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User account was not found"
                        )
                );

        if (!user.isActive()) {
            throw new IllegalStateException(
                    "User account is not active"
            );
        }

        user.recordSuccessfulLogin();

        String accessToken =
                jwtService.generateAccessToken(user);

        UserProfile profile = user.getProfile();

        return new AuthenticationResponse(
                user.getId(),
                profile.getFirstName(),
                profile.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getAccountStatus(),
                accessToken,
                null
        );
    }

    private String normalizeEmail(String email) {
        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}