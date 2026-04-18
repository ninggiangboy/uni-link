package dev.ngb.infrastructure.jdbc.hashtag.mapper;

import dev.ngb.domain.hashtag.model.hashtag.HashtagModeration;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.hashtag.entity.HashtagModerationJdbcEntity;

public final class HashtagModerationJdbcMapper implements JdbcMapper<HashtagModeration, HashtagModerationJdbcEntity> {

    public static final HashtagModerationJdbcMapper INSTANCE = new HashtagModerationJdbcMapper();

    private HashtagModerationJdbcMapper() {}

    @Override
    public HashtagModeration toDomain(HashtagModerationJdbcEntity entity) {
        return HashtagModeration.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getHashtagId(),
                entity.getStatus(),
                entity.getReason());
    }

    @Override
    public HashtagModerationJdbcEntity toJdbc(HashtagModeration domain) {
        return HashtagModerationJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .hashtagId(domain.getHashtagId())
                .status(domain.getStatus())
                .reason(domain.getReason())
                .build();
    }
}
