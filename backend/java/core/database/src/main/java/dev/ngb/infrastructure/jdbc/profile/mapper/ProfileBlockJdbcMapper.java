package dev.ngb.infrastructure.jdbc.profile.mapper;

import dev.ngb.domain.profile.model.graph.ProfileBlock;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileBlockJdbcEntity;

public final class ProfileBlockJdbcMapper implements JdbcMapper<ProfileBlock, ProfileBlockJdbcEntity> {

    public static final ProfileBlockJdbcMapper INSTANCE = new ProfileBlockJdbcMapper();

    private ProfileBlockJdbcMapper() {}

    @Override
    public ProfileBlock toDomain(ProfileBlockJdbcEntity entity) {
        return ProfileBlock.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getBlockerProfileId(),
                entity.getBlockedProfileId()
        );
    }

    @Override
    public ProfileBlockJdbcEntity toJdbc(ProfileBlock domain) {
        return ProfileBlockJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .blockerProfileId(domain.getBlockerProfileId())
                .blockedProfileId(domain.getBlockedProfileId())
                .build();
    }
}

