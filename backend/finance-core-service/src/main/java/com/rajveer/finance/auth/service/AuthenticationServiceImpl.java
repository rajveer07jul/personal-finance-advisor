package com.rajveer.finance.auth.service;

import com.rajveer.finance.auth.dto.AuthenticationResponse;
import com.rajveer.finance.auth.dto.RegisterRequest;
import com.rajveer.finance.auth.mapper.RegistrationMapper;
import com.rajveer.finance.exception.DuplicateResourceException;
import com.rajveer.finance.user.entity.User;
import com.rajveer.finance.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    @Override
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

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

        return registrationMapper.toRegistrationResponse(savedUser);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}