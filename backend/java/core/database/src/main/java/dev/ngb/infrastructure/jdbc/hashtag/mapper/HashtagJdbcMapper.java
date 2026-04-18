package dev.ngb.infrastructure.jdbc.hashtag.mapper;

import dev.ngb.domain.hashtag.model.hashtag.Hashtag;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.hashtag.entity.HashtagJdbcEntity;

public final class HashtagJdbcMapper implements JdbcMapper<Hashtag, HashtagJdbcEntity> {

    public static final HashtagJdbcMapper INSTANCE = new HashtagJdbcMapper();

    private HashtagJdbcMapper() {}

    @Override
    public Hashtag toDomain(HashtagJdbcEntity entity) {
        return Hashtag.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getTag(),
                entity.getNormalizedTag(),
                entity.getUsageCount(),
                entity.getThreadCount()
        );
    }

    @Override
    public HashtagJdbcEntity toJdbc(Hashtag domain) {
        return HashtagJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .tag(domain.getTag())
                .normalizedTag(domain.getNormalizedTag())
                .usageCount(domain.getUsageCount())
                .threadCount(domain.getThreadCount())
                .build();
    }
}

