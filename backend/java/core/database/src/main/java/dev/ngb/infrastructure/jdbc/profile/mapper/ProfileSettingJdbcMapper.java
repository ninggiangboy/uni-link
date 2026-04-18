package dev.ngb.infrastructure.jdbc.profile.mapper;

import dev.ngb.domain.profile.model.setting.ProfileSetting;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileSettingJdbcEntity;

public final class ProfileSettingJdbcMapper implements JdbcMapper<ProfileSetting, ProfileSettingJdbcEntity> {

    public static final ProfileSettingJdbcMapper INSTANCE = new ProfileSettingJdbcMapper();

    private ProfileSettingJdbcMapper() {}

    @Override
    public ProfileSetting toDomain(ProfileSettingJdbcEntity entity) {
        return ProfileSetting.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getProfileId(),
                entity.getAllowMentions(),
                entity.getAllowMessages(),
                entity.getAllowTagging(),
                entity.getShowActivityStatus()
        );
    }

    @Override
    public ProfileSettingJdbcEntity toJdbc(ProfileSetting domain) {
        return ProfileSettingJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .profileId(domain.getProfileId())
                .allowMentions(domain.getAllowMentions())
                .allowMessages(domain.getAllowMessages())
                .allowTagging(domain.getAllowTagging())
                .showActivityStatus(domain.getShowActivityStatus())
                .build();
    }
}

