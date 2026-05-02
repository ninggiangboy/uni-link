package dev.ngb.app.profile.application.usecase.create_profile;

import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileRequest;
import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileResponse;
import dev.ngb.app.shared.public_api.IdentityPublicApi;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.setting.ProfileSetting;
import dev.ngb.domain.profile.model.stats.ProfileStats;
import dev.ngb.domain.profile.model.username.ProfileUsername;
import dev.ngb.domain.profile.repository.ProfileRepository;
import dev.ngb.domain.profile.repository.ProfileSettingRepository;
import dev.ngb.domain.profile.repository.ProfileStatsRepository;
import dev.ngb.domain.profile.repository.ProfileUsernameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;

/*
 * Creates the public Profile aggregate for an authenticated account. Enforces:
 *   - Account must be ACTIVE (cross-module check via IdentityPublicApi).
 *   - Account may only own a single profile (FR-02.1.1).
 *   - Username must be globally unique.
 * On success, also bootstraps the per-profile satellite rows: ProfileStats with
 * zeroed counters, default ProfileSetting, and the initial ProfileUsername history row.
 */
@Slf4j
@RequiredArgsConstructor
public class CreateProfileUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final ProfileStatsRepository profileStatsRepository;
    private final ProfileSettingRepository profileSettingRepository;
    private final ProfileUsernameRepository profileUsernameRepository;
    private final IdentityPublicApi identityPublicApi;

    public CreateProfileResponse execute(Long accountId, CreateProfileRequest request) {
        if (!identityPublicApi.isAccountActive(accountId)) {
            throw ProfileError.ACCOUNT_NOT_ACTIVE.exception();
        }

        if (profileRepository.existsByAccountId(accountId)) {
            log.warn("Create profile failed: accountId={} already owns a profile", accountId);
            throw ProfileError.PROFILE_ALREADY_EXISTS_FOR_ACCOUNT.exception();
        }

        if (profileRepository.existsByUsername(request.username())) {
            throw ProfileError.USERNAME_ALREADY_EXISTS.exception();
        }

        Profile profile = Profile.createForNewAccount(
                accountId,
                request.username(),
                request.displayName(),
                request.bio(),
                request.visibility()
        );

        Profile savedProfile;
        try {
            savedProfile = profileRepository.save(profile);
        } catch (DataIntegrityViolationException e) {
            // Race against the unique-username/account index; surface a friendlier conflict.
            log.warn("Create profile failed: integrity violation accountId={}, username={}", accountId, request.username());
            throw ProfileError.USERNAME_ALREADY_EXISTS.exception();
        }

        profileStatsRepository.save(ProfileStats.createForNewProfile(savedProfile.getId()));
        profileSettingRepository.save(ProfileSetting.createDefault(savedProfile.getId()));
        profileUsernameRepository.save(ProfileUsername.createCurrent(savedProfile.getId(), savedProfile.getUsername()));

        log.info("Profile created profileId={}, profileUuid={}, accountId={}",
                savedProfile.getId(), savedProfile.getUuid(), accountId);

        return new CreateProfileResponse(
                savedProfile.getUuid(),
                savedProfile.getUsername(),
                savedProfile.getDisplayName(),
                savedProfile.getVisibility(),
                savedProfile.getCreatedAt()
        );
    }
}
