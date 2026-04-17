package com.medbooking.auth.service;

import com.medbooking.auth.dto.AuthResponse;
import com.medbooking.auth.dto.LoginRequest;
import com.medbooking.auth.dto.LoginResult;
import com.medbooking.auth.dto.RegisterRequest;

import java.util.UUID;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    LoginResult login(LoginRequest request);

    LoginResult refresh(String refreshTokenValue);

    void logout(String refreshTokenValue);

    void logoutAll(UUID userId);
}
