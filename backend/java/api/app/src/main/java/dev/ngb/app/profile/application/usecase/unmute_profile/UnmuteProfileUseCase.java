package dev.ngb.app.profile.application.usecase.unmute_profile;

import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.repository.ProfileRelationshipRepository;
import dev.ngb.domain.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UnmuteProfileUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final ProfileRelationshipRepository profileRelationshipRepository;

    public void execute(Long accountId, String targetUsername) {
        Profile muter = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);
        Profile target = profileRepository.findByUsername(targetUsername)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);

        boolean deleted = profileRelationshipRepository.unmute(muter.getId(), target.getId());
        if (!deleted) {
            throw ProfileError.NOT_MUTED.exception();
        }
        log.info("Unmute ok muterId={}, targetId={}", muter.getId(), target.getId());
    }
}
