package dev.ngb.infrastructure.jdbc.thread.mapper;

import dev.ngb.domain.thread.model.thread.ThreadMedia;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadMediaJdbcEntity;

public final class ThreadMediaJdbcMapper implements JdbcMapper<ThreadMedia, ThreadMediaJdbcEntity> {

    public static final ThreadMediaJdbcMapper INSTANCE = new ThreadMediaJdbcMapper();

    private ThreadMediaJdbcMapper() {}

    @Override
    public ThreadMedia toDomain(ThreadMediaJdbcEntity entity) {
        return ThreadMedia.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getThreadId(),
                entity.getType(),
                entity.getUrl(),
                entity.getThumbnailUrl(),
                entity.getWidth(),
                entity.getHeight(),
                entity.getDuration(),
                entity.getAltText(),
                entity.getPosition());
    }

    @Override
    public ThreadMediaJdbcEntity toJdbc(ThreadMedia domain) {
        return ThreadMediaJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .threadId(domain.getThreadId())
                .type(domain.getType())
                .url(domain.getUrl())
                .thumbnailUrl(domain.getThumbnailUrl())
                .width(domain.getWidth())
                .height(domain.getHeight())
                .duration(domain.getDuration())
                .altText(domain.getAltText())
                .position(domain.getPosition())
                .build();
    }
}
