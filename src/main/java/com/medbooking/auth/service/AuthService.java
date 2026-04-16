package com.medbooking.auth.service;

import com.medbooking.auth.dto.*;
import com.medbooking.auth.entity.RefreshToken;
import com.medbooking.common.enums.UserRole;
import com.medbooking.common.exception.DuplicateResourceException;
import com.medbooking.common.exception.UnauthorizedException;
import com.medbooking.user.entity.User;
import com.medbooking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.jwt.access-token-expiry-minutes:15}")
    private long accessTokenExpiryMinutes;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("User", "email", request.email());
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .phone(request.phone())
                .role(UserRole.PATIENT)
                .build();

        userRepository.save(user);
        log.info("New patient registered: {}", user.getEmail());

        return new AuthResponse("Đăng ký thành công");
    }

    @Transactional
    public LoginResult login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow();

        String accessToken = tokenService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.create(user);

        log.info("User logged in: {}", user.getEmail());

        return new LoginResult(
                LoginResponse.of(accessToken, accessTokenExpiryMinutes * 60),
                refreshToken.getToken()
        );
    }

    @Transactional
    public LoginResult refresh(String refreshTokenValue) {
        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            throw new UnauthorizedException("Refresh token không tồn tại");
        }

        RefreshToken newToken = refreshTokenService.rotate(refreshTokenValue);
        String accessToken = tokenService.generateAccessToken(newToken.getUser());

        return new LoginResult(
                LoginResponse.of(accessToken, accessTokenExpiryMinutes * 60),
                newToken.getToken()
        );
    }

    @Transactional
    public void logout(String refreshTokenValue) {
        if (refreshTokenValue != null && !refreshTokenValue.isBlank()) {
            refreshTokenService.revoke(refreshTokenValue);
            log.info("User logged out, refresh token revoked");
        }
    }

    @Transactional
    public void logoutAll(UUID userId) {
        refreshTokenService.revokeAllForUser(userId);
        log.info("User logged out from all devices: {}", userId);
    }
}