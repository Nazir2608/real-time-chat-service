package com.nazir.realtimechat.conversation.service;

import com.nazir.realtimechat.common.exception.ResourceNotFoundException;
import com.nazir.realtimechat.conversation.dto.ConversationResponse;
import com.nazir.realtimechat.conversation.entity.Conversation;
import com.nazir.realtimechat.conversation.entity.ConversationMember;
import com.nazir.realtimechat.conversation.repository.ConversationMemberRepository;
import com.nazir.realtimechat.conversation.repository.ConversationRepository;
import com.nazir.realtimechat.user.dto.UserResponse;
import com.nazir.realtimechat.user.entity.User;
import com.nazir.realtimechat.user.repository.UserRepository;
import com.nazir.realtimechat.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public ConversationResponse createDirectConversation(UUID currentUserId, UUID targetUserId) {
        log.info("Creating direct conversation between {} and {}", currentUserId, targetUserId);
        // 1. Check if conversation already exists
        return conversationRepository.findDirectConversationBetweenUsers(currentUserId, targetUserId)
                .map(conv -> {
                    log.info("Found existing conversation: {}", conv.getId());
                    return mapToResponse(conv, targetUserId);
                })
                .orElseGet(() -> {
                    log.info("Creating new conversation");
                    // 2. Create new conversation
                    User currentUser = userRepository.findById(currentUserId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUserId));
                    User targetUser = userRepository.findById(targetUserId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + targetUserId));

                    Conversation conversation = new Conversation();
                    conversation.setType(Conversation.ConversationType.DIRECT);
                    conversation = conversationRepository.save(conversation);

                    // 3. Add members
                    conversationMemberRepository.save(new ConversationMember(conversation, currentUser));
                    conversationMemberRepository.save(new ConversationMember(conversation, targetUser));

                    log.info("Created new conversation with ID: {}", conversation.getId());
                    return mapToResponse(conversation, targetUserId);
                });
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> getUserConversations(UUID userId) {
        log.info("Fetching conversations for user: {}", userId);
        List<ConversationMember> memberships = conversationMemberRepository.findByUserIdSorted(userId);
        log.info("Found {} memberships for user {}", memberships.size(), userId);
        
        return memberships.stream()
                .map(membership -> {
                    Conversation conv = membership.getConversation();
                    UUID otherParticipantId = findOtherParticipantIdFromMembers(conv.getId(), userId);
                    log.info("Conversation {}: other participant is {}", conv.getId(), otherParticipantId);
                    return mapToResponse(conv, otherParticipantId);
                })
                .collect(Collectors.toList());
    }

    private UUID findOtherParticipantIdFromMembers(UUID conversationId, UUID currentUserId) {
        List<ConversationMember> members = conversationMemberRepository.findByConversationId(conversationId);
        return members.stream()
                .filter(m -> !m.getUser().getId().equals(currentUserId))
                .map(m -> m.getUser().getId())
                .findFirst()
                .orElse(null);
    }

    private ConversationResponse mapToResponse(Conversation conversation, UUID otherUserId) {
        UserResponse otherUser = null;
        if (otherUserId != null) {
            try {
                otherUser = userService.getUserById(otherUserId);
            } catch (Exception e) {
                // Ignore if user not found for some reason
            }
        }

        return ConversationResponse.builder()
                .id(conversation.getId())
                .type(conversation.getType())
                .createdAt(conversation.getCreatedAt())
                .lastMessageAt(conversation.getLastMessageAt())
                .otherParticipant(otherUser)
                .build();
    }
}
