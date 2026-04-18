package dev.ngb.infrastructure.jdbc.thread.mapper;

import dev.ngb.domain.thread.model.interaction.ThreadBookmark;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadBookmarkJdbcEntity;

public final class ThreadBookmarkJdbcMapper implements JdbcMapper<ThreadBookmark, ThreadBookmarkJdbcEntity> {

    public static final ThreadBookmarkJdbcMapper INSTANCE = new ThreadBookmarkJdbcMapper();

    private ThreadBookmarkJdbcMapper() {}

    @Override
    public ThreadBookmark toDomain(ThreadBookmarkJdbcEntity entity) {
        return ThreadBookmark.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getThreadId(),
                entity.getProfileId()
        );
    }

    @Override
    public ThreadBookmarkJdbcEntity toJdbc(ThreadBookmark domain) {
        return ThreadBookmarkJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .threadId(domain.getThreadId())
                .profileId(domain.getProfileId())
                .build();
    }
}

