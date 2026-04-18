package dev.ngb.infrastructure.jdbc.notification.mapper;

import dev.ngb.domain.notification.model.notification.NotificationActor;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.notification.entity.NotificationActorJdbcEntity;

public final class NotificationActorJdbcMapper implements JdbcMapper<NotificationActor, NotificationActorJdbcEntity> {

    public static final NotificationActorJdbcMapper INSTANCE = new NotificationActorJdbcMapper();

    private NotificationActorJdbcMapper() {}

    @Override
    public NotificationActor toDomain(NotificationActorJdbcEntity entity) {
        return NotificationActor.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getNotificationId(),
                entity.getActorProfileId());
    }

    @Override
    public NotificationActorJdbcEntity toJdbc(NotificationActor domain) {
        return NotificationActorJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .notificationId(domain.getNotificationId())
                .actorProfileId(domain.getActorProfileId())
                .build();
    }
}
