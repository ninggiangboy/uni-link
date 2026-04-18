package dev.ngb.infrastructure.jdbc.thread.mapper;

import dev.ngb.domain.thread.model.moderation.ThreadModeration;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadModerationJdbcEntity;

public final class ThreadModerationJdbcMapper implements JdbcMapper<ThreadModeration, ThreadModerationJdbcEntity> {

    public static final ThreadModerationJdbcMapper INSTANCE = new ThreadModerationJdbcMapper();

    private ThreadModerationJdbcMapper() {}

    @Override
    public ThreadModeration toDomain(ThreadModerationJdbcEntity entity) {
        return ThreadModeration.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getThreadId(),
                entity.getReporterProfileId(),
                entity.getReason(),
                entity.getStatus(),
                entity.getModeratorProfileId(),
                entity.getResolution()
        );
    }

    @Override
    public ThreadModerationJdbcEntity toJdbc(ThreadModeration domain) {
        return ThreadModerationJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .threadId(domain.getThreadId())
                .reporterProfileId(domain.getReporterProfileId())
                .reason(domain.getReason())
                .status(domain.getStatus())
                .moderatorProfileId(domain.getModeratorProfileId())
                .resolution(domain.getResolution())
                .build();
    }
}

