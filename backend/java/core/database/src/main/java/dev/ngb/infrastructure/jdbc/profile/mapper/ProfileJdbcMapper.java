package dev.ngb.infrastructure.jdbc.profile.mapper;

import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileJdbcEntity;

public final class ProfileJdbcMapper implements JdbcMapper<Profile, ProfileJdbcEntity> {

    public static final ProfileJdbcMapper INSTANCE = new ProfileJdbcMapper();

    private ProfileJdbcMapper() {}

    @Override
    public Profile toDomain(ProfileJdbcEntity entity) {
        return Profile.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getAccountId(),
                entity.getUsername(),
                entity.getDisplayName(),
                entity.getBio(),
                entity.getWebsite(),
                entity.getLocation(),
                entity.getAvatarUrl(),
                entity.getBannerUrl(),
                entity.getVisibility(),
                entity.getIsVerified(),
                entity.getPublicKey()
        );
    }

    @Override
    public ProfileJdbcEntity toJdbc(Profile domain) {
        return ProfileJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .accountId(domain.getAccountId())
                .username(domain.getUsername())
                .displayName(domain.getDisplayName())
                .bio(domain.getBio())
                .website(domain.getWebsite())
                .location(domain.getLocation())
                .avatarUrl(domain.getAvatarUrl())
                .bannerUrl(domain.getBannerUrl())
                .visibility(domain.getVisibility())
                .isVerified(domain.getIsVerified())
                .publicKey(domain.getPublicKey())
                .build();
    }
}

