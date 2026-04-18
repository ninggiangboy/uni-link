package dev.ngb.infrastructure.jdbc.notification.mapper;

import dev.ngb.domain.notification.model.notification.Notification;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.notification.entity.NotificationJdbcEntity;

public final class NotificationJdbcMapper implements JdbcMapper<Notification, NotificationJdbcEntity> {

    public static final NotificationJdbcMapper INSTANCE = new NotificationJdbcMapper();

    private NotificationJdbcMapper() {}

    @Override
    public Notification toDomain(NotificationJdbcEntity entity) {
        return Notification.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getRecipientProfileId(),
                entity.getActorProfileId(),
                entity.getType(),
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getIsRead(),
                entity.getGroupKey(),
                entity.getActorCount(),
                entity.getLastActorProfileId()
        );
    }

    @Override
    public NotificationJdbcEntity toJdbc(Notification domain) {
        return NotificationJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .recipientProfileId(domain.getRecipientProfileId())
                .actorProfileId(domain.getActorProfileId())
                .type(domain.getType())
                .entityType(domain.getEntityType())
                .entityId(domain.getEntityId())
                .isRead(domain.getIsRead())
                .groupKey(domain.getGroupKey())
                .actorCount(domain.getActorCount())
                .lastActorProfileId(domain.getLastActorProfileId())
                .build();
    }
}

