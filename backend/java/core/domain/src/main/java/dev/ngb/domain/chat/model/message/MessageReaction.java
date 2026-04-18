package dev.ngb.domain.chat.model.message;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * An emoji reaction on a message.
 */
@Getter
public class MessageReaction extends DomainEntity<Long> {

    private MessageReaction() {}

    private Long messageId;
    private Long profileId;
    private String reaction;

    public static MessageReaction reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long messageId, Long profileId, String reaction) {
        MessageReaction obj = new MessageReaction();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.messageId = messageId;
        obj.profileId = profileId;
        obj.reaction = reaction;
        return obj;
    }
}
