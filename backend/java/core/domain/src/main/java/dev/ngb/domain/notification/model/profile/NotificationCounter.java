package dev.ngb.domain.notification.model.profile;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Materialized unread notification count per profile, backed by Redis cache at runtime.
 */
@Getter
public class NotificationCounter extends DomainEntity<Long> {

    private NotificationCounter() {}

    private Long profileId;
    private Long unreadCount;

    public static NotificationCounter reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long profileId, Long unreadCount) {
        NotificationCounter obj = new NotificationCounter();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.profileId = profileId;
        obj.unreadCount = unreadCount;
        return obj;
    }
}
