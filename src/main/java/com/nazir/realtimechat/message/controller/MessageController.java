package com.nazir.realtimechat.message.controller;

import com.nazir.realtimechat.common.dto.ApiResponse;
import com.nazir.realtimechat.message.dto.MessageRequest;
import com.nazir.realtimechat.message.dto.MessageResponse;
import com.nazir.realtimechat.message.service.MessageService;
import com.nazir.realtimechat.user.entity.User;
import com.nazir.realtimechat.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody MessageRequest request) {
        log.info("Request to send message from user {} to conversation {}", userDetails.getUsername(), request.getConversationId());
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        MessageResponse response = messageService.sendMessage(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.<MessageResponse>builder()
                .success(true)
                .data(response)
                .message("Message sent successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessages(
            @AuthenticationPrincipal UserDetails userDetails, @RequestParam UUID conversationId,
            @RequestParam(required = false) Instant before, @RequestParam(defaultValue = "20") int limit) {

        log.info("Request to fetch messages for conversation {} by user {}", conversationId, userDetails.getUsername());
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        List<MessageResponse> messages = messageService.getMessages(currentUser.getId(), conversationId, before, limit);
        
        return ResponseEntity.ok(ApiResponse.<List<MessageResponse>>builder()
                .success(true)
                .data(messages)
                .message("Messages fetched successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }
}
