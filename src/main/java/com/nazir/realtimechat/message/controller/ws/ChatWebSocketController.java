package com.nazir.realtimechat.message.controller.ws;

import com.nazir.realtimechat.message.dto.MessageRequest;
import com.nazir.realtimechat.message.dto.MessageResponse;
import com.nazir.realtimechat.message.service.MessageService;
import com.nazir.realtimechat.user.entity.User;
import com.nazir.realtimechat.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.security.Principal;
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
        log.info("WebSocket message received from {} for conversation {}", principal.getName(), request.getConversationId());

        try {
            // 1. Find the current user
            User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));

            // 2. Save the message using the existing service logic
            MessageResponse savedMessage = messageService.sendMessage(currentUser.getId(), request);

            // 3. Broadcast the saved message to the specific conversation topic
            String destination = "/topic/conversation." + request.getConversationId();
            log.info("Broadcasting message {} to {}", savedMessage.getId(), destination);
            
            messagingTemplate.convertAndSend(destination, savedMessage);

        } catch (Exception e) {
            log.error("Error handling WebSocket message: {}", e.getMessage(), e);
        }
    }
}
