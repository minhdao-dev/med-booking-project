package com.medbooking.auth.repository;

import com.medbooking.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByTokenAndIsRevokedFalse(String token);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.user.id = :userId AND rt.isRevoked = false")
    void revokeAllByUserId(@Param("userId") UUID userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.familyId = :familyId AND rt.isRevoked = false")
    void revokeAllByFamilyId(@Param("familyId") UUID familyId);
}