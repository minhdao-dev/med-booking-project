package com.medbooking.auth.service;

import com.medbooking.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenFamilyRevoker {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeFamily(UUID familyId) {
        refreshTokenRepository.revokeAllByFamilyId(familyId);
        log.warn("Token family revoked due to reuse detection — family: {}", familyId);
    }
}