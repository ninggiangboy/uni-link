package dev.ngb.domain.chat.model.conversation;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * A chat conversation (direct or group).
 * <p>
 * Aggregate root of the conversation domain.
 * Messages form a separate aggregate. Blocks and inbox index are cross-aggregate.
 */
@Getter
public class Conversation extends DomainEntity<Long> {

    private Conversation() {}

    private ConversationType type;
    private Long createdByProfileId;
    private Long lastMessageId;
    private Instant lastMessageAt;

    public static Conversation reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            ConversationType type, Long createdByProfileId, Long lastMessageId, Instant lastMessageAt) {
        Conversation obj = new Conversation();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.type = type;
        obj.createdByProfileId = createdByProfileId;
        obj.lastMessageId = lastMessageId;
        obj.lastMessageAt = lastMessageAt;
        return obj;
    }
}
