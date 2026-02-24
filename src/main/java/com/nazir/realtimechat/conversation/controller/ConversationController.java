package com.nazir.realtimechat.conversation.controller;

import com.nazir.realtimechat.common.dto.ApiResponse;
import com.nazir.realtimechat.conversation.dto.ConversationRequest;
import com.nazir.realtimechat.conversation.dto.ConversationResponse;
import com.nazir.realtimechat.conversation.service.ConversationService;
import com.nazir.realtimechat.user.entity.User;
import com.nazir.realtimechat.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<ConversationResponse>> createConversation(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody ConversationRequest request) {
        log.info("Request to create conversation with target user {} from authenticated user {}", request.getTargetUserId(), userDetails.getUsername());
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        ConversationResponse response = conversationService.createDirectConversation(currentUser.getId(), request.getTargetUserId());
        return ResponseEntity.ok(ApiResponse.<ConversationResponse>builder()
                .success(true)
                .data(response)
                .message("Conversation created successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getConversations(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Request to fetch conversations for authenticated user {}", userDetails.getUsername());
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        List<ConversationResponse> conversations = conversationService.getUserConversations(currentUser.getId());
        log.info("Successfully fetched {} conversations for user {}", conversations.size(), userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.<List<ConversationResponse>>builder()
                .success(true)
                .data(conversations)
                .message("Conversations fetched successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }
}
