package dev.ngb.infrastructure.jdbc.thread.mapper;

import dev.ngb.domain.thread.model.thread.ThreadPoll;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadPollJdbcEntity;

public final class ThreadPollJdbcMapper implements JdbcMapper<ThreadPoll, ThreadPollJdbcEntity> {

    public static final ThreadPollJdbcMapper INSTANCE = new ThreadPollJdbcMapper();

    private ThreadPollJdbcMapper() {}

    @Override
    public ThreadPoll toDomain(ThreadPollJdbcEntity entity) {
        return ThreadPoll.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getThreadId(),
                entity.getExpiresAt(),
                entity.getAllowMultipleVotes());
    }

    @Override
    public ThreadPollJdbcEntity toJdbc(ThreadPoll domain) {
        return ThreadPollJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .threadId(domain.getThreadId())
                .expiresAt(domain.getExpiresAt())
                .allowMultipleVotes(domain.getAllowMultipleVotes())
                .build();
    }
}
