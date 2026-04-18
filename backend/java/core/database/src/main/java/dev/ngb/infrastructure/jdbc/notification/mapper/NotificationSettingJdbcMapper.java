package dev.ngb.infrastructure.jdbc.notification.mapper;

import dev.ngb.domain.notification.model.profile.NotificationSetting;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.notification.entity.NotificationSettingJdbcEntity;

public final class NotificationSettingJdbcMapper implements JdbcMapper<NotificationSetting, NotificationSettingJdbcEntity> {

    public static final NotificationSettingJdbcMapper INSTANCE = new NotificationSettingJdbcMapper();

    private NotificationSettingJdbcMapper() {}

    @Override
    public NotificationSetting toDomain(NotificationSettingJdbcEntity entity) {
        return NotificationSetting.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getProfileId(),
                entity.getLikeEnabled(),
                entity.getReplyEnabled(),
                entity.getMentionEnabled(),
                entity.getFollowEnabled(),
                entity.getRepostEnabled(),
                entity.getQuoteEnabled(),
                entity.getPushEnabled(),
                entity.getEmailEnabled()
        );
    }

    @Override
    public NotificationSettingJdbcEntity toJdbc(NotificationSetting domain) {
        return NotificationSettingJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .profileId(domain.getProfileId())
                .likeEnabled(domain.getLikeEnabled())
                .replyEnabled(domain.getReplyEnabled())
                .mentionEnabled(domain.getMentionEnabled())
                .followEnabled(domain.getFollowEnabled())
                .repostEnabled(domain.getRepostEnabled())
                .quoteEnabled(domain.getQuoteEnabled())
                .pushEnabled(domain.getPushEnabled())
                .emailEnabled(domain.getEmailEnabled())
                .build();
    }
}

