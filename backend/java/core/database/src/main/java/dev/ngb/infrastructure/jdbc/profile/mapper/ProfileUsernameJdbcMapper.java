package dev.ngb.infrastructure.jdbc.profile.mapper;

import dev.ngb.domain.profile.model.username.ProfileUsername;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileUsernameJdbcEntity;

public final class ProfileUsernameJdbcMapper implements JdbcMapper<ProfileUsername, ProfileUsernameJdbcEntity> {

    public static final ProfileUsernameJdbcMapper INSTANCE = new ProfileUsernameJdbcMapper();

    private ProfileUsernameJdbcMapper() {}

    @Override
    public ProfileUsername toDomain(ProfileUsernameJdbcEntity entity) {
        return ProfileUsername.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getProfileId(),
                entity.getUsername(),
                entity.getIsCurrent()
        );
    }

    @Override
    public ProfileUsernameJdbcEntity toJdbc(ProfileUsername domain) {
        return ProfileUsernameJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .profileId(domain.getProfileId())
                .username(domain.getUsername())
                .isCurrent(domain.getIsCurrent())
                .build();
    }
}

