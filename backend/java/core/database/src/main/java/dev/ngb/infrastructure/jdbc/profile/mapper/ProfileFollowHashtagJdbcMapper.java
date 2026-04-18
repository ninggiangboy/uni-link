package dev.ngb.infrastructure.jdbc.profile.mapper;

import dev.ngb.domain.profile.model.graph.ProfileFollowHashtag;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileFollowHashtagJdbcEntity;

public final class ProfileFollowHashtagJdbcMapper implements JdbcMapper<ProfileFollowHashtag, ProfileFollowHashtagJdbcEntity> {

    public static final ProfileFollowHashtagJdbcMapper INSTANCE = new ProfileFollowHashtagJdbcMapper();

    private ProfileFollowHashtagJdbcMapper() {}

    @Override
    public ProfileFollowHashtag toDomain(ProfileFollowHashtagJdbcEntity entity) {
        return ProfileFollowHashtag.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getProfileId(),
                entity.getHashtagId()
        );
    }

    @Override
    public ProfileFollowHashtagJdbcEntity toJdbc(ProfileFollowHashtag domain) {
        return ProfileFollowHashtagJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .profileId(domain.getProfileId())
                .hashtagId(domain.getHashtagId())
                .build();
    }
}

