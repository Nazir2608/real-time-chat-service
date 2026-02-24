package com.nazir.realtimechat.message.service;

import com.nazir.realtimechat.common.exception.BadRequestException;
import com.nazir.realtimechat.common.exception.ResourceNotFoundException;
import com.nazir.realtimechat.common.exception.UnauthorizedException;
import com.nazir.realtimechat.conversation.entity.Conversation;
import com.nazir.realtimechat.conversation.repository.ConversationMemberRepository;
import com.nazir.realtimechat.conversation.repository.ConversationRepository;
import com.nazir.realtimechat.message.dto.MessageRequest;
import com.nazir.realtimechat.message.dto.MessageResponse;
import com.nazir.realtimechat.message.entity.Message;
import com.nazir.realtimechat.message.repository.MessageRepository;
import com.nazir.realtimechat.user.entity.User;
import com.nazir.realtimechat.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public MessageResponse sendMessage(UUID senderId, MessageRequest request) {
        log.info("User {} sending message to conversation {}", senderId, request.getConversationId());
        // 1. Verify user is a member of the conversation
        if (!conversationMemberRepository.existsByConversationIdAndUserId(request.getConversationId(), senderId)) {
            log.warn("User {} attempted to send message to conversation {} without membership", senderId, request.getConversationId());
            throw new UnauthorizedException("You are not a member of this conversation");
        }
        // 2. Get conversation and sender
        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        // 3. Create and save message
        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(request.getContent());
        message.setStatus(Message.MessageStatus.SENT);

        Message savedMessage = messageRepository.save(message);
        
        // 4. Update conversation last message timestamp
        conversation.setLastMessageAt(savedMessage.getCreatedAt());
        conversationRepository.save(conversation);
        
        log.info("Message {} saved successfully in conversation {}", savedMessage.getId(), conversation.getId());

        return mapToResponse(savedMessage);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(UUID userId, UUID conversationId, Instant before, int limit) {
        log.info("User {} fetching {} messages for conversation {} before {}", userId, limit, conversationId, before);
        // 1. Verify user is a member of the conversation
        if (!conversationMemberRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            log.warn("User {} attempted to fetch messages for conversation {} without membership", userId, conversationId);
            throw new UnauthorizedException("You are not a member of this conversation");
        }
        // 2. Fetch messages using cursor-based pagination
        Pageable pageable = PageRequest.of(0, limit);
        List<Message> messages = messageRepository.findMessagesBefore(conversationId, before, pageable);
        log.info("Query returned {} messages for conversation {}", messages.size(), conversationId);

        return messages.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(UUID userId, UUID conversationId) {
        log.info("User {} marking messages as READ in conversation {}", userId, conversationId);
        // 1. Verify membership
        if (!conversationMemberRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            throw new UnauthorizedException("You are not a member of this conversation");
        }
        // 2. Update status
        int updatedCount = messageRepository.markMessagesAsRead(conversationId, userId, Message.MessageStatus.READ);
        log.info("Marked {} messages as READ for user {} in conversation {}", updatedCount, userId, conversationId);
    }

    private MessageResponse mapToResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSender().getId())
                .content(message.getContent())
                .status(message.getStatus())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
