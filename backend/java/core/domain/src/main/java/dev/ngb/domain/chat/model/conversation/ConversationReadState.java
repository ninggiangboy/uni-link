package dev.ngb.domain.chat.model.conversation;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Tracks the last read message per participant, more scalable than per-message read receipts.
 */
@Getter
public class ConversationReadState extends DomainEntity<Long> {

    private ConversationReadState() {}

    private Long conversationId;
    private Long profileId;
    private Long lastReadMessageId;

    public static ConversationReadState reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long conversationId, Long profileId, Long lastReadMessageId) {
        ConversationReadState obj = new ConversationReadState();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.conversationId = conversationId;
        obj.profileId = profileId;
        obj.lastReadMessageId = lastReadMessageId;
        return obj;
    }
}
