package dev.ngb.infrastructure.jdbc.thread.mapper;

import dev.ngb.domain.thread.model.thread.ThreadLinkPreview;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadLinkPreviewJdbcEntity;

public final class ThreadLinkPreviewJdbcMapper implements JdbcMapper<ThreadLinkPreview, ThreadLinkPreviewJdbcEntity> {

    public static final ThreadLinkPreviewJdbcMapper INSTANCE = new ThreadLinkPreviewJdbcMapper();

    private ThreadLinkPreviewJdbcMapper() {}

    @Override
    public ThreadLinkPreview toDomain(ThreadLinkPreviewJdbcEntity entity) {
        return ThreadLinkPreview.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getThreadId(),
                entity.getUrl(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getImageUrl());
    }

    @Override
    public ThreadLinkPreviewJdbcEntity toJdbc(ThreadLinkPreview domain) {
        return ThreadLinkPreviewJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .threadId(domain.getThreadId())
                .url(domain.getUrl())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .imageUrl(domain.getImageUrl())
                .build();
    }
}
