package dev.ngb.domain.notification.model.notification;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * References an entity related to a notification, allowing multiple entity references per notification.
 */
@Getter
public class NotificationObject extends DomainEntity<Long> {

    private NotificationObject() {}

    private Long notificationId;
    private NotificationEntityType entityType;
    private Long entityId;

    public static NotificationObject reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long notificationId, NotificationEntityType entityType, Long entityId) {
        NotificationObject obj = new NotificationObject();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.notificationId = notificationId;
        obj.entityType = entityType;
        obj.entityId = entityId;
        return obj;
    }
}
