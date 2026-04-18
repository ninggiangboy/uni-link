package dev.ngb.domain.chat.model.moderation;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Represents a profile blocking another within a conversation context.
 */
@Getter
public class ConversationBlock extends DomainEntity<Long> {

    private ConversationBlock() {}

    private Long blockerProfileId;
    private Long blockedProfileId;
    private Long conversationId;

    public static ConversationBlock reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long blockerProfileId, Long blockedProfileId, Long conversationId) {
        ConversationBlock obj = new ConversationBlock();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.blockerProfileId = blockerProfileId;
        obj.blockedProfileId = blockedProfileId;
        obj.conversationId = conversationId;
        return obj;
    }
}
