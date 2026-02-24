package com.nazir.realtimechat.user.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private Instant createdAt;
    private Instant updatedAt;
}
