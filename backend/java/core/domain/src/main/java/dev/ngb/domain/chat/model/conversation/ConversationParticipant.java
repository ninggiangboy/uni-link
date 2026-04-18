package dev.ngb.domain.chat.model.conversation;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * A participant in a conversation, with role and join/leave tracking.
 */
@Getter
public class ConversationParticipant extends DomainEntity<Long> {

    private ConversationParticipant() {}

    private Long conversationId;
    private Long profileId;
    private ConversationRole role;
    private Instant joinedAt;
    private Instant leftAt;

    public static ConversationParticipant reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long conversationId, Long profileId, ConversationRole role, Instant joinedAt, Instant leftAt) {
        ConversationParticipant obj = new ConversationParticipant();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.conversationId = conversationId;
        obj.profileId = profileId;
        obj.role = role;
        obj.joinedAt = joinedAt;
        obj.leftAt = leftAt;
        return obj;
    }
}
