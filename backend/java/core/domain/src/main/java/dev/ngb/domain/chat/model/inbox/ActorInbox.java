package dev.ngb.domain.chat.model.inbox;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Denormalized inbox index per profile, enabling fast inbox loading with unread counts, pin, and mute state.
 */
@Getter
public class ActorInbox extends DomainEntity<Long> {

    private ActorInbox() {}

    private Long profileId;
    private Long conversationId;
    private Long lastMessageId;
    private Long lastReadMessageId;
    private Long unreadCount;
    private Boolean pinned;
    private Boolean muted;

    public static ActorInbox reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long profileId, Long conversationId, Long lastMessageId, Long lastReadMessageId,
            Long unreadCount, Boolean pinned, Boolean muted) {
        ActorInbox obj = new ActorInbox();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.profileId = profileId;
        obj.conversationId = conversationId;
        obj.lastMessageId = lastMessageId;
        obj.lastReadMessageId = lastReadMessageId;
        obj.unreadCount = unreadCount;
        obj.pinned = pinned;
        obj.muted = muted;
        return obj;
    }
}
