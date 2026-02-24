package com.nazir.realtimechat.conversation.repository;

import com.nazir.realtimechat.conversation.entity.ConversationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationMemberRepository extends JpaRepository<ConversationMember, UUID> {
    @Query("""
        SELECT m FROM ConversationMember m 
        JOIN m.conversation c 
        WHERE m.user.id = :userId 
        ORDER BY COALESCE(c.lastMessageAt, c.createdAt) DESC
    """)
    List<ConversationMember> findByUserIdSorted(@Param("userId") UUID userId);

    List<ConversationMember> findByUserId(UUID userId);
    List<ConversationMember> findByConversationId(UUID conversationId);
    Optional<ConversationMember> findByConversationIdAndUserId(UUID conversationId, UUID userId);
    boolean existsByConversationIdAndUserId(UUID conversationId, UUID userId);
}
