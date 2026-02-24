package com.nazir.realtimechat.message.repository;

import com.nazir.realtimechat.message.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    /**
     * Cursor-based pagination for messages in a conversation.
     * Returns messages created before the given timestamp, ordered by creation date descending.
     */
    @Query("SELECT m FROM Message m " +
           "WHERE m.conversation.id = :conversationId " +
           "AND (:before IS NULL OR m.createdAt < :before) " +
           "ORDER BY m.createdAt DESC")
    List<Message> findMessagesBefore(@Param("conversationId") UUID conversationId, @Param("before") Instant before, Pageable pageable);

    /**
     * Marks all messages in a conversation as READ for a specific user (the recipient).
     * This updates messages where the sender is NOT the current user and status is NOT READ.
     */
    @Modifying
    @Query("UPDATE Message m SET m.status = :status " +
           "WHERE m.conversation.id = :conversationId " +
           "AND m.sender.id != :userId " +
           "AND m.status != :status")
    int markMessagesAsRead(@Param("conversationId") UUID conversationId, @Param("userId") UUID userId, @Param("status") Message.MessageStatus status);
}
