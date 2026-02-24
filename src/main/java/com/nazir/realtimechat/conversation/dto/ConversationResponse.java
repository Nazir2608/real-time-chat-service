package com.nazir.realtimechat.conversation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nazir.realtimechat.conversation.entity.Conversation;
import com.nazir.realtimechat.user.dto.UserResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class ConversationResponse {
    private UUID id;
    private Conversation.ConversationType type;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
    private Instant createdAt;
    
    private UserResponse otherParticipant;
}
