package com.medbooking.auth.service;

import com.medbooking.auth.entity.RefreshToken;
import com.medbooking.user.entity.User;

import java.util.UUID;

public interface RefreshTokenService {
    RefreshToken create(User user);

    RefreshToken rotate(String oldTokenValue);

    void revoke(String tokenValue);

    void revokeAllForUser(UUID userId);
}