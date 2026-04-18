package dev.ngb.infrastructure.jdbc.profile.mapper;

import dev.ngb.domain.profile.model.stats.ProfileStats;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileStatsJdbcEntity;

public final class ProfileStatsJdbcMapper implements JdbcMapper<ProfileStats, ProfileStatsJdbcEntity> {

    public static final ProfileStatsJdbcMapper INSTANCE = new ProfileStatsJdbcMapper();

    private ProfileStatsJdbcMapper() {}

    @Override
    public ProfileStats toDomain(ProfileStatsJdbcEntity entity) {
        return ProfileStats.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getProfileId(),
                entity.getFollowerCount(),
                entity.getFollowingCount(),
                entity.getThreadCount(),
                entity.getLikeCount(),
                entity.getMediaCount()
        );
    }

    @Override
    public ProfileStatsJdbcEntity toJdbc(ProfileStats domain) {
        return ProfileStatsJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .profileId(domain.getProfileId())
                .followerCount(domain.getFollowerCount())
                .followingCount(domain.getFollowingCount())
                .threadCount(domain.getThreadCount())
                .likeCount(domain.getLikeCount())
                .mediaCount(domain.getMediaCount())
                .build();
    }
}

