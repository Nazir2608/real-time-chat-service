package com.nazir.realtimechat.message.controller.ws;

import com.nazir.realtimechat.message.dto.MessageRequest;
import com.nazir.realtimechat.message.dto.MessageResponse;
import com.nazir.realtimechat.message.dto.TypingRequest;
import com.nazir.realtimechat.message.service.MessageService;
import com.nazir.realtimechat.user.entity.User;
import com.nazir.realtimechat.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.security.Principal;
import java.util.UUID;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    /**
     * Handles real-time messages sent via WebSocket.
     * Destination: /app/chat.send
     * Broadcasts to: /topic/conversation.{conversationId}
     */
    @MessageMapping("/chat.send")
    public void handleMessage(Principal principal, @Payload MessageRequest request) {
        log.info("WebSocket message received from {} for conversation {}", 
                principal.getName(), request.getConversationId());

        try {
            // 1. Find the current user
            User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));

            // 2. Save message via service
            MessageResponse response = messageService.sendMessage(currentUser.getId(), request);

            // 3. Broadcast to the conversation topic
            String topic = "/topic/conversation." + request.getConversationId();
            messagingTemplate.convertAndSend(topic, response);
            log.info("Message broadcasted to topic: {}", topic);

        } catch (Exception e) {
            log.error("Failed to process WebSocket message: {}", e.getMessage());
        }
    }

    /**
     * Handles read receipts.
     * Destination: /app/chat.read
     */
    @MessageMapping("/chat.read")
    public void handleReadReceipt(Principal principal, @Payload UUID conversationId) {
        log.info("Read receipt received from {} for conversation {}", principal.getName(), conversationId);
        
        try {
            User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));

            // 1. Mark as read in DB
            messageService.markAsRead(currentUser.getId(), conversationId);

            // 2. Broadcast read event to the topic
            String topic = "/topic/conversation." + conversationId;
            messagingTemplate.convertAndSend(topic, new ReadReceipt(conversationId, currentUser.getId()));
            log.info("Read receipt broadcasted to topic: {}", topic);

        } catch (Exception e) {
            log.error("Failed to process read receipt: {}", e.getMessage());
        }
    }

    /**
     * Handles typing indicators.
     * Destination: /app/chat.typing
     */
    @MessageMapping("/chat.typing")
    public void handleTyping(Principal principal, @Payload TypingRequest request) {
        log.info("Typing event received from {} for conversation {}: {}", 
                principal.getName(), request.getConversationId(), request.isTyping());
        
        try {
            User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
            
            String topic = "/topic/conversation." + request.getConversationId();
            messagingTemplate.convertAndSend(topic, new TypingEvent(request.getConversationId(), currentUser.getId(), request.isTyping()));
            
        } catch (Exception e) {
            log.error("Failed to process typing event: {}", e.getMessage());
        }
    }

    @lombok.Data
    public static class ReadReceipt {
        private UUID conversationId;
        private UUID readerId;
        private String type = "READ_RECEIPT";

        public ReadReceipt(UUID conversationId, UUID readerId) {
            this.conversationId = conversationId;
            this.readerId = readerId;
        }
    }

    @lombok.Data
    public static class TypingEvent {
        private UUID conversationId;
        private UUID userId;
        private boolean isTyping;
        private String type = "TYPING";

        public TypingEvent(UUID conversationId, UUID userId, boolean isTyping) {
            this.conversationId = conversationId;
            this.userId = userId;
            this.isTyping = isTyping;
        }
    }
}
