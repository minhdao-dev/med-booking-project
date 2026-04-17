package com.medbooking.auth.service.impl;

import com.medbooking.auth.entity.RefreshToken;
import com.medbooking.auth.repository.RefreshTokenRepository;
import com.medbooking.auth.service.RefreshTokenService;
import com.medbooking.common.exception.UnauthorizedException;
import com.medbooking.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-token-expiry-days:7}")
    private long refreshTokenExpiryDays;

    @Transactional
    @Override
    public RefreshToken create(User user) {
        return createInFamily(user, UUID.randomUUID());
    }

    @Transactional
    @Override
    public RefreshToken rotate(String oldTokenValue) {
        RefreshToken oldToken = refreshTokenRepository.findByToken(oldTokenValue)
                .orElseThrow(() -> new UnauthorizedException("Xác thực thất bại"));

        if (Boolean.TRUE.equals(oldToken.getIsRevoked())) {
            UUID familyId = oldToken.getFamilyId();
            UUID userId = oldToken.getUser().getId();

            log.warn("REFRESH TOKEN REUSE DETECTED — user: {}, family: {}. Revoking entire family.",
                    userId, familyId);

            refreshTokenRepository.revokeAllByFamilyId(familyId);

            throw new UnauthorizedException("Xác thực thất bại");
        }

        if (oldToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Xác thực thất bại");
        }

        oldToken.setIsRevoked(true);
        refreshTokenRepository.save(oldToken);

        return createInFamily(oldToken.getUser(), oldToken.getFamilyId());
    }

    @Transactional
    @Override
    public void revoke(String tokenValue) {
        refreshTokenRepository.findByTokenAndIsRevokedFalse(tokenValue)
                .ifPresent(token -> {
                    token.setIsRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    @Transactional
    @Override
    public void revokeAllForUser(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
        log.info("All refresh tokens revoked for user: {}", userId);
    }

    private RefreshToken createInFamily(User user, UUID familyId) {
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .familyId(familyId)
                .expiresAt(LocalDateTime.now().plusDays(refreshTokenExpiryDays))
                .build();

        return refreshTokenRepository.save(token);
    }
}