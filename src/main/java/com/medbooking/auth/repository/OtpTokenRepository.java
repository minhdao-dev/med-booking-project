package com.medbooking.auth.repository;

import com.medbooking.auth.entity.OtpToken;
import com.medbooking.common.enums.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    Optional<OtpToken> findByUserIdAndOtpCodeAndTypeAndIsUsedFalse(
            UUID userId, String otpCode, OtpType type
    );
}