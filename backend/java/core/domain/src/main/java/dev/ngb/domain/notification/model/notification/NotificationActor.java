package dev.ngb.domain.notification.model.notification;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Tracks individual actors within a grouped notification.
 */
@Getter
public class NotificationActor extends DomainEntity<Long> {

    private NotificationActor() {}

    private Long notificationId;
    private Long actorProfileId;

    public static NotificationActor reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long notificationId, Long actorProfileId) {
        NotificationActor obj = new NotificationActor();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.notificationId = notificationId;
        obj.actorProfileId = actorProfileId;
        return obj;
    }
}
