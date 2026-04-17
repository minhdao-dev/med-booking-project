package com.medbooking.auth.controller;

import com.medbooking.auth.dto.*;
import com.medbooking.auth.service.AuthService;
import com.medbooking.common.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private final AuthService authService;

    @Value("${app.jwt.refresh-token-expiry-days:7}")
    private long refreshTokenExpiryDays;

    @Value("${app.cookie.secure:false}")
    private boolean secureCookie;

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản bệnh nhân")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        LoginResult result = authService.login(request);
        setRefreshTokenCookie(response, result.refreshToken());
        return ResponseEntity.ok(result.loginResponse());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Làm mới access token bằng refresh token (cookie)")
    public ResponseEntity<LoginResponse> refresh(
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshToken,
            HttpServletResponse response) {

        LoginResult result = authService.refresh(refreshToken);
        setRefreshTokenCookie(response, result.refreshToken());
        return ResponseEntity.ok(result.loginResponse());
    }

    @PostMapping("/logout")
    @Operation(summary = "Đăng xuất, revoke refresh token")
    public ResponseEntity<AuthResponse> logout(
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshToken,
            HttpServletResponse response) {

        authService.logout(refreshToken);
        clearRefreshTokenCookie(response);
        return ResponseEntity.ok(new AuthResponse("Đăng xuất thành công"));
    }

    @PostMapping("/logout-all")
    @Operation(
            summary = "Đăng xuất khỏi tất cả thiết bị",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    public ResponseEntity<AuthResponse> logoutAll(HttpServletResponse response) {
        UUID userId = SecurityUtils.getCurrentUserId();
        authService.logoutAll(userId);
        clearRefreshTokenCookie(response);
        return ResponseEntity.ok(new AuthResponse("Đã đăng xuất khỏi tất cả thiết bị"));
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(refreshTokenExpiryDays * 24 * 60 * 60)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}