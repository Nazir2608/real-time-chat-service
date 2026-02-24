package com.nazir.realtimechat.message.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nazir.realtimechat.message.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private UUID id;
    private UUID conversationId;
    private UUID senderId;
    private String content;
    private Message.MessageStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
    private Instant createdAt;
}
