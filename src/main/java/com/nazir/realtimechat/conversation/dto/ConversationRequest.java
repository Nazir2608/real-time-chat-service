package com.nazir.realtimechat.conversation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ConversationRequest {
    @NotNull
    private UUID targetUserId;
}
