package com.medbooking.common.util;

import com.medbooking.common.enums.UserRole;
import com.medbooking.common.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {
        // Prevent instantiation
    }

    public static UUID getCurrentUserId() {
        Jwt jwt = getCurrentJwt();
        return UUID.fromString(jwt.getSubject());
    }

    public static String getCurrentUserEmail() {
        Jwt jwt = getCurrentJwt();
        return jwt.getClaim("email");
    }

    public static UserRole getCurrentUserRole() {
        Jwt jwt = getCurrentJwt();
        String role = jwt.getClaim("role");
        return UserRole.valueOf(role);
    }

    public static boolean hasRole(UserRole role) {
        return getCurrentUserRole() == role;
    }

    private static Jwt getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new UnauthorizedException("Người dùng chưa đăng nhập");
        }

        return jwt;
    }
}