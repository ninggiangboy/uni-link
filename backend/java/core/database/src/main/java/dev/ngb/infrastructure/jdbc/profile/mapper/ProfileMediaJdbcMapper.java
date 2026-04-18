package dev.ngb.infrastructure.jdbc.profile.mapper;

import dev.ngb.domain.profile.model.profile.ProfileMedia;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileMediaJdbcEntity;

public final class ProfileMediaJdbcMapper implements JdbcMapper<ProfileMedia, ProfileMediaJdbcEntity> {

    public static final ProfileMediaJdbcMapper INSTANCE = new ProfileMediaJdbcMapper();

    private ProfileMediaJdbcMapper() {}

    @Override
    public ProfileMedia toDomain(ProfileMediaJdbcEntity entity) {
        return ProfileMedia.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getProfileId(),
                entity.getType(),
                entity.getUrl(),
                entity.getMetadata()
        );
    }

    @Override
    public ProfileMediaJdbcEntity toJdbc(ProfileMedia domain) {
        return ProfileMediaJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .profileId(domain.getProfileId())
                .type(domain.getType())
                .url(domain.getUrl())
                .metadata(domain.getMetadata())
                .build();
    }
}

