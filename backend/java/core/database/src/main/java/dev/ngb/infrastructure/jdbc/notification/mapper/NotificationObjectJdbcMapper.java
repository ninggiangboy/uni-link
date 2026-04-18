package dev.ngb.infrastructure.jdbc.notification.mapper;

import dev.ngb.domain.notification.model.notification.NotificationObject;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.notification.entity.NotificationObjectJdbcEntity;

public final class NotificationObjectJdbcMapper implements JdbcMapper<NotificationObject, NotificationObjectJdbcEntity> {

    public static final NotificationObjectJdbcMapper INSTANCE = new NotificationObjectJdbcMapper();

    private NotificationObjectJdbcMapper() {}

    @Override
    public NotificationObject toDomain(NotificationObjectJdbcEntity entity) {
        return NotificationObject.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getNotificationId(),
                entity.getEntityType(),
                entity.getEntityId());
    }

    @Override
    public NotificationObjectJdbcEntity toJdbc(NotificationObject domain) {
        return NotificationObjectJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .notificationId(domain.getNotificationId())
                .entityType(domain.getEntityType())
                .entityId(domain.getEntityId())
                .build();
    }
}
