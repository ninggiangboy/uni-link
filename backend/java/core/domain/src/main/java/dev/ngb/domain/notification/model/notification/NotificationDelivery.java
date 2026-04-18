package dev.ngb.domain.notification.model.notification;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Tracks delivery status of a notification across channels (in-app, push, email).
 */
@Getter
public class NotificationDelivery extends DomainEntity<Long> {

    private NotificationDelivery() {}

    private Long notificationId;
    private NotificationChannel channel;
    private NotificationDeliveryStatus status;
    private Instant sentAt;

    public static NotificationDelivery reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long notificationId, NotificationChannel channel, NotificationDeliveryStatus status, Instant sentAt) {
        NotificationDelivery obj = new NotificationDelivery();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.notificationId = notificationId;
        obj.channel = channel;
        obj.status = status;
        obj.sentAt = sentAt;
        return obj;
    }
}
