package dev.ngb.app.profile.application.usecase.profile.update_profile;

import dev.ngb.app.profile.application.dto.ProfileSummary;
import dev.ngb.app.profile.application.usecase.profile.update_profile.dto.UpdateProfileRequest;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.stats.ProfileStats;
import dev.ngb.domain.profile.repository.ProfileRepository;
import dev.ngb.domain.profile.repository.ProfileStatsRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateProfileUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final ProfileStatsRepository profileStatsRepository;

    public ProfileSummary execute(Long accountId, UpdateProfileRequest request) {
        Profile profile = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);
        profile.updateInfo(request.displayName(), request.bio(), request.website(), request.location());
        Profile saved = profileRepository.save(profile);

        ProfileStats stats = profileStatsRepository.findByProfileId(saved.getId()).orElse(null);
        return ProfileSummary.of(saved, stats);
    }
}
