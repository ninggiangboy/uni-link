package dev.ngb.infrastructure.jdbc.hashtag.mapper;

import dev.ngb.domain.hashtag.model.hashtag.HashtagStats;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.hashtag.entity.HashtagStatsJdbcEntity;

public final class HashtagStatsJdbcMapper implements JdbcMapper<HashtagStats, HashtagStatsJdbcEntity> {

    public static final HashtagStatsJdbcMapper INSTANCE = new HashtagStatsJdbcMapper();

    private HashtagStatsJdbcMapper() {}

    @Override
    public HashtagStats toDomain(HashtagStatsJdbcEntity entity) {
        return HashtagStats.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getHashtagId(),
                entity.getThreadCount(),
                entity.getUsageCount(),
                entity.getLastUsedAt());
    }

    @Override
    public HashtagStatsJdbcEntity toJdbc(HashtagStats domain) {
        return HashtagStatsJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .hashtagId(domain.getHashtagId())
                .threadCount(domain.getThreadCount())
                .usageCount(domain.getUsageCount())
                .lastUsedAt(domain.getLastUsedAt())
                .build();
    }
}
