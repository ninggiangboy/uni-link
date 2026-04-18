package dev.ngb.infrastructure.jdbc.hashtag.mapper;

import dev.ngb.domain.hashtag.model.hashtag.HashtagMetadata;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.hashtag.entity.HashtagMetadataJdbcEntity;

public final class HashtagMetadataJdbcMapper implements JdbcMapper<HashtagMetadata, HashtagMetadataJdbcEntity> {

    public static final HashtagMetadataJdbcMapper INSTANCE = new HashtagMetadataJdbcMapper();

    private HashtagMetadataJdbcMapper() {}

    @Override
    public HashtagMetadata toDomain(HashtagMetadataJdbcEntity entity) {
        return HashtagMetadata.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getHashtagId(),
                entity.getCategory(),
                entity.getLanguage(),
                entity.getDescription());
    }

    @Override
    public HashtagMetadataJdbcEntity toJdbc(HashtagMetadata domain) {
        return HashtagMetadataJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .hashtagId(domain.getHashtagId())
                .category(domain.getCategory())
                .language(domain.getLanguage())
                .description(domain.getDescription())
                .build();
    }
}
