package dev.ngb.infrastructure.jdbc.hashtag.mapper;

import dev.ngb.domain.hashtag.model.analytics.HashtagRelation;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.hashtag.entity.HashtagRelationJdbcEntity;

public final class HashtagRelationJdbcMapper implements JdbcMapper<HashtagRelation, HashtagRelationJdbcEntity> {

    public static final HashtagRelationJdbcMapper INSTANCE = new HashtagRelationJdbcMapper();

    private HashtagRelationJdbcMapper() {}

    @Override
    public HashtagRelation toDomain(HashtagRelationJdbcEntity entity) {
        return HashtagRelation.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getHashtagAId(),
                entity.getHashtagBId(),
                entity.getCoOccurrenceCount()
        );
    }

    @Override
    public HashtagRelationJdbcEntity toJdbc(HashtagRelation domain) {
        return HashtagRelationJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .hashtagAId(domain.getHashtagAId())
                .hashtagBId(domain.getHashtagBId())
                .coOccurrenceCount(domain.getCoOccurrenceCount())
                .build();
    }
}

