package com.medbooking.auth.dto;

public record LoginResult(
        LoginResponse loginResponse,
        String refreshToken
) {
}