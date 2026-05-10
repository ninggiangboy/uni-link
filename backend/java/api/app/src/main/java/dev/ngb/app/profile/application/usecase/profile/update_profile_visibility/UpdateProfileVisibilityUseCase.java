package dev.ngb.app.profile.application.usecase.profile.update_profile_visibility;

import dev.ngb.app.profile.application.usecase.profile.update_profile_visibility.dto.UpdateProfileVisibilityRequest;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateProfileVisibilityUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;

    public void execute(Long accountId, UpdateProfileVisibilityRequest request) {
        Profile profile = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);
        profile.changeVisibility(request.visibility());
        profileRepository.save(profile);
    }
}
