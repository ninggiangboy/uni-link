package dev.ngb.domain.notification.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.notification.model.notification.Notification;

/**
 * Repository for managing {@link Notification} aggregates.
 */
public interface NotificationRepository extends Repository<Notification, Long> {
}
