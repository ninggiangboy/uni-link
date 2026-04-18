package dev.ngb.domain.chat.model.message;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * A message within a conversation, supporting text, media, thread shares, and edit/delete.
 * <p>
 * Aggregate root for message-level data.
 * Reports are a moderation concern managed separately.
 */
@Getter
public class Message extends DomainEntity<Long> {

    private Message() {}

    private Long conversationId;
    private Long senderProfileId;
    private MessageType type;
    private String content;
    private String iv;
    private Instant editedAt;
    private Instant deletedAt;

    public static Message reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long conversationId, Long senderProfileId, MessageType type, String content, String iv,
            Instant editedAt, Instant deletedAt) {
        Message obj = new Message();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.conversationId = conversationId;
        obj.senderProfileId = senderProfileId;
        obj.type = type;
        obj.content = content;
        obj.iv = iv;
        obj.editedAt = editedAt;
        obj.deletedAt = deletedAt;
        return obj;
    }
}
