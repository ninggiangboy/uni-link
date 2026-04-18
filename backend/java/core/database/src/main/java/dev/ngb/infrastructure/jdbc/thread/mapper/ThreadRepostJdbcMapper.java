package dev.ngb.infrastructure.jdbc.thread.mapper;

import dev.ngb.domain.thread.model.interaction.ThreadRepost;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadRepostJdbcEntity;

public final class ThreadRepostJdbcMapper implements JdbcMapper<ThreadRepost, ThreadRepostJdbcEntity> {

    public static final ThreadRepostJdbcMapper INSTANCE = new ThreadRepostJdbcMapper();

    private ThreadRepostJdbcMapper() {}

    @Override
    public ThreadRepost toDomain(ThreadRepostJdbcEntity entity) {
        return ThreadRepost.reconstruct(
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
    public ThreadRepostJdbcEntity toJdbc(ThreadRepost domain) {
        return ThreadRepostJdbcEntity.builder()
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

