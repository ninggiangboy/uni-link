package dev.ngb.domain.notification.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.notification.model.profile.NotificationCounter;

/**
 * Repository for managing {@link NotificationCounter} per-profile unread counters.
 */
public interface NotificationCounterRepository extends Repository<NotificationCounter, Long> {
}
