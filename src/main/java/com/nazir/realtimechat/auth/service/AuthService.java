package com.nazir.realtimechat.auth.service;

import com.nazir.realtimechat.auth.dto.AuthResponse;
import com.nazir.realtimechat.auth.dto.LoginRequest;
import com.nazir.realtimechat.auth.dto.RegisterRequest;
import com.nazir.realtimechat.common.exception.BadRequestException;
import com.nazir.realtimechat.common.util.JwtUtil;
import com.nazir.realtimechat.user.entity.User;
import com.nazir.realtimechat.user.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());
        claims.put("username", user.getUsername());
        String access = jwtUtil.generateAccessToken(user.getUsername(), claims);
        String refresh = jwtUtil.generateRefreshToken(user.getUsername(), claims);
        return AuthResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .build();
    }
}
