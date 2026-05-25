package com.ruralhealthcare.security;

import com.ruralhealthcare.entity.RefreshToken;
import com.ruralhealthcare.entity.User;
import com.ruralhealthcare.exception.TokenRefreshException;
import com.ruralhealthcare.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    // FIXED: Manual Constructor for Spring Dependency Injection (No Lombok needed)
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        refreshTokenRepository.revokeAllUserTokens(user);

        // FIXED: Using standard object instantiation and setters instead of Lombok Builder
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000));
        token.setRevoked(false);

        return refreshTokenRepository.save(token);
    }

    @Transactional
    public RefreshToken verifyAndRotate(String rawToken) {
        RefreshToken existing = refreshTokenRepository.findByToken(rawToken)
                .orElseThrow(() -> new TokenRefreshException("Refresh token not found"));

        if (existing.isRevoked()) {
            refreshTokenRepository.revokeAllUserTokens(existing.getUser());
            throw new TokenRefreshException("Refresh token was reused. All sessions invalidated for security.");
        }

        if (existing.isExpired()) {
            existing.setRevoked(true);
            refreshTokenRepository.save(existing);
            throw new TokenRefreshException("Refresh token has expired. Please login again.");
        }

        existing.setRevoked(true);
        refreshTokenRepository.save(existing);

        return createRefreshToken(existing.getUser());
    }

    @Transactional
    public void revokeAll(User user) {
        refreshTokenRepository.revokeAllUserTokens(user);
    }
}