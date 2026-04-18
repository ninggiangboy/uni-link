package dev.ngb.infrastructure.jdbc.profile.mapper;

import dev.ngb.domain.profile.model.activity.ProfileActivity;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileActivityJdbcEntity;

public final class ProfileActivityJdbcMapper implements JdbcMapper<ProfileActivity, ProfileActivityJdbcEntity> {

    public static final ProfileActivityJdbcMapper INSTANCE = new ProfileActivityJdbcMapper();

    private ProfileActivityJdbcMapper() {}

    @Override
    public ProfileActivity toDomain(ProfileActivityJdbcEntity entity) {
        return ProfileActivity.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getProfileId(),
                entity.getLastActiveAt(),
                entity.getLastPostAt()
        );
    }

    @Override
    public ProfileActivityJdbcEntity toJdbc(ProfileActivity domain) {
        return ProfileActivityJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .profileId(domain.getProfileId())
                .lastActiveAt(domain.getLastActiveAt())
                .lastPostAt(domain.getLastPostAt())
                .build();
    }
}

