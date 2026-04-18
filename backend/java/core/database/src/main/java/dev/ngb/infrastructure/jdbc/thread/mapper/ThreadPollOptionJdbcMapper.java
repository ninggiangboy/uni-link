package dev.ngb.infrastructure.jdbc.thread.mapper;

import dev.ngb.domain.thread.model.thread.ThreadPollOption;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadPollOptionJdbcEntity;

public final class ThreadPollOptionJdbcMapper implements JdbcMapper<ThreadPollOption, ThreadPollOptionJdbcEntity> {

    public static final ThreadPollOptionJdbcMapper INSTANCE = new ThreadPollOptionJdbcMapper();

    private ThreadPollOptionJdbcMapper() {}

    @Override
    public ThreadPollOption toDomain(ThreadPollOptionJdbcEntity entity) {
        return ThreadPollOption.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getThreadId(),
                entity.getOption(),
                entity.getPosition(),
                entity.getVoteCount());
    }

    @Override
    public ThreadPollOptionJdbcEntity toJdbc(ThreadPollOption domain) {
        return ThreadPollOptionJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .threadId(domain.getThreadId())
                .option(domain.getOption())
                .position(domain.getPosition())
                .voteCount(domain.getVoteCount())
                .build();
    }
}
