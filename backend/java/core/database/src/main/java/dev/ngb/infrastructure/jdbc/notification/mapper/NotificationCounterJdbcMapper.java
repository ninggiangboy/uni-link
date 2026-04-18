package dev.ngb.infrastructure.jdbc.notification.mapper;

import dev.ngb.domain.notification.model.profile.NotificationCounter;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.notification.entity.NotificationCounterJdbcEntity;

public final class NotificationCounterJdbcMapper implements JdbcMapper<NotificationCounter, NotificationCounterJdbcEntity> {

    public static final NotificationCounterJdbcMapper INSTANCE = new NotificationCounterJdbcMapper();

    private NotificationCounterJdbcMapper() {}

    @Override
    public NotificationCounter toDomain(NotificationCounterJdbcEntity entity) {
        return NotificationCounter.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getProfileId(),
                entity.getUnreadCount()
        );
    }

    @Override
    public NotificationCounterJdbcEntity toJdbc(NotificationCounter domain) {
        return NotificationCounterJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .profileId(domain.getProfileId())
                .unreadCount(domain.getUnreadCount())
                .build();
    }
}

