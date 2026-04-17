package com.medbooking.auth.service.impl;

import com.medbooking.auth.service.TokenService;
import com.medbooking.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.access-token-expiry-minutes:15}")
    private long accessTokenExpiryMinutes;

    @Override
    public String generateAccessToken(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("medbooking")
                .issuedAt(now)
                .expiresAt(now.plus(accessTokenExpiryMinutes, ChronoUnit.MINUTES))
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}