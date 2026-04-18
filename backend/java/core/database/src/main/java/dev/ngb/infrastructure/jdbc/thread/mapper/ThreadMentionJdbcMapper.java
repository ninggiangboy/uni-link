package dev.ngb.infrastructure.jdbc.thread.mapper;

import dev.ngb.domain.thread.model.thread.ThreadMention;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadMentionJdbcEntity;

public final class ThreadMentionJdbcMapper implements JdbcMapper<ThreadMention, ThreadMentionJdbcEntity> {

    public static final ThreadMentionJdbcMapper INSTANCE = new ThreadMentionJdbcMapper();

    private ThreadMentionJdbcMapper() {}

    @Override
    public ThreadMention toDomain(ThreadMentionJdbcEntity entity) {
        return ThreadMention.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getThreadId(),
                entity.getMentionedProfileId());
    }

    @Override
    public ThreadMentionJdbcEntity toJdbc(ThreadMention domain) {
        return ThreadMentionJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .threadId(domain.getThreadId())
                .mentionedProfileId(domain.getMentionedProfileId())
                .build();
    }
}
