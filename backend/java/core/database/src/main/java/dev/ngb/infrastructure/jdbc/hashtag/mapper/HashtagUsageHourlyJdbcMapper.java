package dev.ngb.infrastructure.jdbc.hashtag.mapper;

import dev.ngb.domain.hashtag.model.analytics.HashtagUsageHourly;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.hashtag.entity.HashtagUsageHourlyJdbcEntity;

public final class HashtagUsageHourlyJdbcMapper implements JdbcMapper<HashtagUsageHourly, HashtagUsageHourlyJdbcEntity> {

    public static final HashtagUsageHourlyJdbcMapper INSTANCE = new HashtagUsageHourlyJdbcMapper();

    private HashtagUsageHourlyJdbcMapper() {}

    @Override
    public HashtagUsageHourly toDomain(HashtagUsageHourlyJdbcEntity entity) {
        return HashtagUsageHourly.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getHashtagId(),
                entity.getHourBucket(),
                entity.getCount()
        );
    }

    @Override
    public HashtagUsageHourlyJdbcEntity toJdbc(HashtagUsageHourly domain) {
        return HashtagUsageHourlyJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .hashtagId(domain.getHashtagId())
                .hourBucket(domain.getHourBucket())
                .count(domain.getCount())
                .build();
    }
}

