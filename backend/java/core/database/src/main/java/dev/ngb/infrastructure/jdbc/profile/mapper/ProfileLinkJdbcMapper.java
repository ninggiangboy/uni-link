package dev.ngb.infrastructure.jdbc.profile.mapper;

import dev.ngb.domain.profile.model.profile.ProfileLink;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileLinkJdbcEntity;

public final class ProfileLinkJdbcMapper implements JdbcMapper<ProfileLink, ProfileLinkJdbcEntity> {

    public static final ProfileLinkJdbcMapper INSTANCE = new ProfileLinkJdbcMapper();

    private ProfileLinkJdbcMapper() {}

    @Override
    public ProfileLink toDomain(ProfileLinkJdbcEntity entity) {
        return ProfileLink.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getProfileId(),
                entity.getType(),
                entity.getUrl(),
                entity.getOrderIndex()
        );
    }

    @Override
    public ProfileLinkJdbcEntity toJdbc(ProfileLink domain) {
        return ProfileLinkJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .profileId(domain.getProfileId())
                .type(domain.getType())
                .url(domain.getUrl())
                .orderIndex(domain.getOrderIndex())
                .build();
    }
}

