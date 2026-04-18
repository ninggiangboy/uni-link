package dev.ngb.infrastructure.jdbc.thread.mapper;

import dev.ngb.domain.thread.model.interaction.ThreadLike;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadLikeJdbcEntity;

public final class ThreadLikeJdbcMapper implements JdbcMapper<ThreadLike, ThreadLikeJdbcEntity> {

    public static final ThreadLikeJdbcMapper INSTANCE = new ThreadLikeJdbcMapper();

    private ThreadLikeJdbcMapper() {}

    @Override
    public ThreadLike toDomain(ThreadLikeJdbcEntity entity) {
        return ThreadLike.reconstruct(
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
    public ThreadLikeJdbcEntity toJdbc(ThreadLike domain) {
        return ThreadLikeJdbcEntity.builder()
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

