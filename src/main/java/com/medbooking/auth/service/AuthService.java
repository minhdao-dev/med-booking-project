package com.medbooking.auth.service;

import com.medbooking.auth.dto.AuthResponse;
import com.medbooking.auth.dto.RegisterRequest;
import com.medbooking.common.enums.UserRole;
import com.medbooking.common.exception.DuplicateResourceException;
import com.medbooking.user.entity.User;
import com.medbooking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
}