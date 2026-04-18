package dev.ngb.infrastructure.jdbc.thread.mapper;

import dev.ngb.domain.thread.model.interaction.ThreadPollVote;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadPollVoteJdbcEntity;

public final class ThreadPollVoteJdbcMapper implements JdbcMapper<ThreadPollVote, ThreadPollVoteJdbcEntity> {

    public static final ThreadPollVoteJdbcMapper INSTANCE = new ThreadPollVoteJdbcMapper();

    private ThreadPollVoteJdbcMapper() {}

    @Override
    public ThreadPollVote toDomain(ThreadPollVoteJdbcEntity entity) {
        return ThreadPollVote.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getThreadId(),
                entity.getProfileId(),
                entity.getOptionIndex()
        );
    }

    @Override
    public ThreadPollVoteJdbcEntity toJdbc(ThreadPollVote domain) {
        return ThreadPollVoteJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .threadId(domain.getThreadId())
                .profileId(domain.getProfileId())
                .optionIndex(domain.getOptionIndex())
                .build();
    }
}

