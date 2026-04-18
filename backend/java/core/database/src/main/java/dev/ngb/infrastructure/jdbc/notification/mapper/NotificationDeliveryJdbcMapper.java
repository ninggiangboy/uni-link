package dev.ngb.infrastructure.jdbc.notification.mapper;

import dev.ngb.domain.notification.model.notification.NotificationDelivery;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.notification.entity.NotificationDeliveryJdbcEntity;

public final class NotificationDeliveryJdbcMapper implements JdbcMapper<NotificationDelivery, NotificationDeliveryJdbcEntity> {

    public static final NotificationDeliveryJdbcMapper INSTANCE = new NotificationDeliveryJdbcMapper();

    private NotificationDeliveryJdbcMapper() {}

    @Override
    public NotificationDelivery toDomain(NotificationDeliveryJdbcEntity entity) {
        return NotificationDelivery.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getNotificationId(),
                entity.getChannel(),
                entity.getStatus(),
                entity.getSentAt());
    }

    @Override
    public NotificationDeliveryJdbcEntity toJdbc(NotificationDelivery domain) {
        return NotificationDeliveryJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .notificationId(domain.getNotificationId())
                .channel(domain.getChannel())
                .status(domain.getStatus())
                .sentAt(domain.getSentAt())
                .build();
    }
}
