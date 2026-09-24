package com.rajveer.finance.auth.service;

import com.rajveer.finance.auth.entity.RefreshToken;
import com.rajveer.finance.auth.repository.RefreshTokenRepository;
import com.rajveer.finance.exception.BusinessRuleException;
import com.rajveer.finance.security.jwt.JwtProperties;
import com.rajveer.finance.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl
        implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public String createRefreshToken(User user) {
        String rawToken = generateSecureToken();
        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(
                        LocalDateTime.now().plusSeconds(
                                jwtProperties.refreshTokenExpiration()
                        )
                )
                .build();

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    @Override
    @Transactional
    public User validateAndRotate(String rawToken) {
        RefreshToken storedToken =
                findByRawToken(rawToken);

        validateToken(storedToken);

        storedToken.revoke();

        return storedToken.getUser();
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String rawToken) {
        RefreshToken storedToken =
                findByRawToken(rawToken);

        if (!storedToken.isRevoked()) {
            storedToken.revoke();
        }
    }

    private RefreshToken findByRawToken(String rawToken) {
        String tokenHash = hashToken(rawToken);

        return refreshTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(
                        () -> new BusinessRuleException(
                                "Refresh token is invalid"
                        )
                );
    }

    private void validateToken(RefreshToken refreshToken) {
        if (refreshToken.isRevoked()) {
            throw new BusinessRuleException(
                    "Refresh token has already been revoked"
            );
        }

        if (refreshToken.isExpired()) {
            throw new BusinessRuleException(
                    "Refresh token has expired"
            );
        }

        if (!refreshToken.getUser().isActive()) {
            throw new BusinessRuleException(
                    "User account is not active"
            );
        }
    }

    private String generateSecureToken() {
        String randomValue =
                UUID.randomUUID()
                        + "."
                        + UUID.randomUUID()
                        + "."
                        + UUID.randomUUID();

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        randomValue.getBytes(StandardCharsets.UTF_8)
                );
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest messageDigest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = messageDigest.digest(
                    rawToken.getBytes(StandardCharsets.UTF_8)
            );

            return convertToHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 hashing algorithm is unavailable",
                    exception
            );
        }
    }

    private String convertToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();

        for (byte currentByte : bytes) {
            result.append(
                    String.format("%02x", currentByte)
            );
        }

        return result.toString();
    }
}