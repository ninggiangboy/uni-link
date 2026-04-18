package dev.ngb.infrastructure.jdbc.profile.mapper;

import dev.ngb.domain.profile.model.graph.ProfileFollow;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileFollowJdbcEntity;

public final class ProfileFollowJdbcMapper implements JdbcMapper<ProfileFollow, ProfileFollowJdbcEntity> {

    public static final ProfileFollowJdbcMapper INSTANCE = new ProfileFollowJdbcMapper();

    private ProfileFollowJdbcMapper() {}

    @Override
    public ProfileFollow toDomain(ProfileFollowJdbcEntity entity) {
        return ProfileFollow.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getFollowerProfileId(),
                entity.getFollowingProfileId()
        );
    }

    @Override
    public ProfileFollowJdbcEntity toJdbc(ProfileFollow domain) {
        return ProfileFollowJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .followerProfileId(domain.getFollowerProfileId())
                .followingProfileId(domain.getFollowingProfileId())
                .build();
    }
}

