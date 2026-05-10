package dev.ngb.app.profile.application.usecase.profile.update_profile_setting;

import dev.ngb.app.profile.application.dto.ProfileSettingResponse;
import dev.ngb.app.profile.application.usecase.profile.update_profile_setting.dto.UpdateProfileSettingRequest;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.setting.ProfileSetting;
import dev.ngb.domain.profile.repository.ProfileRepository;
import dev.ngb.domain.profile.repository.ProfileSettingRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateProfileSettingUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final ProfileSettingRepository profileSettingRepository;

    public ProfileSettingResponse execute(Long accountId, UpdateProfileSettingRequest request) {
        Profile profile = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);
        ProfileSetting setting = profileSettingRepository.findByProfileId(profile.getId())
                .orElseGet(() -> ProfileSetting.createDefault(profile.getId()));
        setting.update(request.allowMentions(), request.allowMessages(), request.allowTagging(), request.showActivityStatus());
        ProfileSetting saved = profileSettingRepository.save(setting);
        return ProfileSettingResponse.of(saved);
    }
}
