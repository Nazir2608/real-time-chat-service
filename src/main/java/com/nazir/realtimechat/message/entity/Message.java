package com.nazir.realtimechat.message.entity;

import com.nazir.realtimechat.common.entity.BaseEntity;
import com.nazir.realtimechat.conversation.entity.Conversation;
import com.nazir.realtimechat.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "messages",
        indexes = {
                @Index(name = "idx_msg_conv_created", columnList = "conversation_id, created_at DESC"),
                @Index(name = "idx_msg_sender", columnList = "sender_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Message extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageStatus status = MessageStatus.SENT;

    public enum MessageStatus {
        SENT, DELIVERED, READ
    }
}
