package dev.ngb.domain.notification.model.notification;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * A notification delivered to a profile, with support for grouping multiple actors
 * (e.g. 'Alice and 12 others liked your thread').
 * <p>
 * Aggregate root of the notification domain.
 * Settings and counters are per-profile entities managed separately.
 */
@Getter
public class Notification extends DomainEntity<Long> {

    private Notification() {}

    private Long recipientProfileId;
    private Long actorProfileId;

    private NotificationType type;
    private NotificationEntityType entityType;
    private Long entityId;

    private Boolean isRead;

    private String groupKey;
    private Long actorCount;
    private Long lastActorProfileId;

    public static Notification reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long recipientProfileId, Long actorProfileId, NotificationType type, NotificationEntityType entityType,
            Long entityId, Boolean isRead, String groupKey, Long actorCount, Long lastActorProfileId) {
        Notification obj = new Notification();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.recipientProfileId = recipientProfileId;
        obj.actorProfileId = actorProfileId;
        obj.type = type;
        obj.entityType = entityType;
        obj.entityId = entityId;
        obj.isRead = isRead;
        obj.groupKey = groupKey;
        obj.actorCount = actorCount;
        obj.lastActorProfileId = lastActorProfileId;
        return obj;
    }
}
