package com.nazir.realtimechat.user.controller;

import com.nazir.realtimechat.common.dto.ApiResponse;
import com.nazir.realtimechat.user.dto.UserResponse;
import com.nazir.realtimechat.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponse user = userService.getUserByUsername(userDetails.getUsername());
        ApiResponse<UserResponse> resp = ApiResponse.<UserResponse>builder()
                .success(true)
                .data(user)
                .message("User fetched successfully")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        UserResponse user = userService.getUserById(id);
        ApiResponse<UserResponse> resp = ApiResponse.<UserResponse>builder()
                .success(true)
                .data(user)
                .message("User fetched successfully")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(resp);
    }
}
