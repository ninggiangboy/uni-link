package dev.ngb.infrastructure.jdbc.profile.mapper;

import dev.ngb.domain.profile.model.profile.ProfileMetadata;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileMetadataJdbcEntity;

public final class ProfileMetadataJdbcMapper implements JdbcMapper<ProfileMetadata, ProfileMetadataJdbcEntity> {

    public static final ProfileMetadataJdbcMapper INSTANCE = new ProfileMetadataJdbcMapper();

    private ProfileMetadataJdbcMapper() {}

    @Override
    public ProfileMetadata toDomain(ProfileMetadataJdbcEntity entity) {
        return ProfileMetadata.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getProfileId(),
                entity.getKey(),
                entity.getValue()
        );
    }

    @Override
    public ProfileMetadataJdbcEntity toJdbc(ProfileMetadata domain) {
        return ProfileMetadataJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .profileId(domain.getProfileId())
                .key(domain.getKey())
                .value(domain.getValue())
                .build();
    }
}

