package com.medbooking.auth.service;

import com.medbooking.user.entity.User;

public interface TokenService {
    String generateAccessToken(User user);
}
