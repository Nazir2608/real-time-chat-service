package com.nazir.realtimechat.auth.controller;

import com.nazir.realtimechat.auth.dto.AuthResponse;
import com.nazir.realtimechat.auth.dto.LoginRequest;
import com.nazir.realtimechat.auth.dto.RegisterRequest;
import com.nazir.realtimechat.auth.service.AuthService;
import com.nazir.realtimechat.common.dto.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Received registration request for username: {}", request.getUsername());
        authService.register(request);
        log.info("User {} registered successfully", request.getUsername());
        ApiResponse<Void> resp = ApiResponse.<Void>builder()
                .success(true)
                .message("User registered successfully")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login request for user: {}", request.getUsernameOrEmail());
        AuthResponse tokens = authService.login(request);
        log.info("User {} logged in successfully", request.getUsernameOrEmail());
        ApiResponse<AuthResponse> resp = ApiResponse.<AuthResponse>builder()
                .success(true)
                .data(tokens)
                .message("Login successful")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(resp);
    }
}
