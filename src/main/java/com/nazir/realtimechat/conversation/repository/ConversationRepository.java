package com.nazir.realtimechat.conversation.repository;

import com.nazir.realtimechat.conversation.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    @Query("""
        SELECT c FROM Conversation c 
        WHERE c.type = 'DIRECT' 
        AND c.id IN (SELECT m1.conversation.id FROM ConversationMember m1 WHERE m1.user.id = :user1Id) 
        AND c.id IN (SELECT m2.conversation.id FROM ConversationMember m2 WHERE m2.user.id = :user2Id)
    """)
    Optional<Conversation> findDirectConversationBetweenUsers(@Param("user1Id") UUID user1Id, @Param("user2Id") UUID user2Id);
}
