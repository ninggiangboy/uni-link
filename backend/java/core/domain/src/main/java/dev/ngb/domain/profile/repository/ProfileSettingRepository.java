package dev.ngb.domain.profile.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.profile.model.setting.ProfileSetting;

import java.util.Optional;

/**
 * Repository for managing {@link ProfileSetting} entities (separate from Profile aggregate).
 */
public interface ProfileSettingRepository extends Repository<ProfileSetting, Long> {

    Optional<ProfileSetting> findByProfileId(Long profileId);
}
