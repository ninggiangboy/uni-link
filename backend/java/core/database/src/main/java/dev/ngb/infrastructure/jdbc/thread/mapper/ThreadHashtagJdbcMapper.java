package dev.ngb.infrastructure.jdbc.thread.mapper;

import dev.ngb.domain.thread.model.thread.ThreadHashtag;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadHashtagJdbcEntity;

public final class ThreadHashtagJdbcMapper implements JdbcMapper<ThreadHashtag, ThreadHashtagJdbcEntity> {

    public static final ThreadHashtagJdbcMapper INSTANCE = new ThreadHashtagJdbcMapper();

    private ThreadHashtagJdbcMapper() {}

    @Override
    public ThreadHashtag toDomain(ThreadHashtagJdbcEntity entity) {
        return ThreadHashtag.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getThreadId(),
                entity.getHashtagId(),
                entity.getPositionStart(),
                entity.getPositionEnd());
    }

    @Override
    public ThreadHashtagJdbcEntity toJdbc(ThreadHashtag domain) {
        return ThreadHashtagJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .threadId(domain.getThreadId())
                .hashtagId(domain.getHashtagId())
                .positionStart(domain.getPositionStart())
                .positionEnd(domain.getPositionEnd())
                .build();
    }
}
