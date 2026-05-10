package dev.ngb.app.profile.application.usecase.social.mute_profile;

import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.repository.ProfileRelationshipRepository;
import dev.ngb.domain.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MuteProfileUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final ProfileRelationshipRepository profileRelationshipRepository;

    public void execute(Long accountId, String targetUsername) {
        Profile muter = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);
        Profile target = profileRepository.findByUsername(targetUsername)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);

        if (muter.getId().equals(target.getId())) {
            throw ProfileError.CANNOT_MUTE_SELF.exception();
        }

        boolean created = profileRelationshipRepository.mute(
                muter.getId(), target.getId());
        if (!created) {
            throw ProfileError.ALREADY_MUTED.exception();
        }
        log.info("Mute created muterId={}, targetId={}", muter.getId(), target.getId());
    }
}
