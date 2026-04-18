package dev.ngb.infrastructure.jdbc.profile.mapper;

import dev.ngb.domain.profile.model.graph.ProfileMute;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileMuteJdbcEntity;

public final class ProfileMuteJdbcMapper implements JdbcMapper<ProfileMute, ProfileMuteJdbcEntity> {

    public static final ProfileMuteJdbcMapper INSTANCE = new ProfileMuteJdbcMapper();

    private ProfileMuteJdbcMapper() {}

    @Override
    public ProfileMute toDomain(ProfileMuteJdbcEntity entity) {
        return ProfileMute.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getMuterProfileId(),
                entity.getMutedProfileId()
        );
    }

    @Override
    public ProfileMuteJdbcEntity toJdbc(ProfileMute domain) {
        return ProfileMuteJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .muterProfileId(domain.getMuterProfileId())
                .mutedProfileId(domain.getMutedProfileId())
                .build();
    }
}

