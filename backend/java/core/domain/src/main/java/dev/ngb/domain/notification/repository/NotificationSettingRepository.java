package dev.ngb.domain.notification.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.notification.model.profile.NotificationSetting;

/**
 * Repository for managing {@link NotificationSetting} per-profile settings.
 */
public interface NotificationSettingRepository extends Repository<NotificationSetting, Long> {
}
