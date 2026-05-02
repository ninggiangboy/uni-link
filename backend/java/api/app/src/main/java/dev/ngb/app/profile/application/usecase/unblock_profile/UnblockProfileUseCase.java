package dev.ngb.app.profile.application.usecase.unblock_profile;

import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.repository.ProfileRelationshipRepository;
import dev.ngb.domain.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UnblockProfileUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final ProfileRelationshipRepository profileRelationshipRepository;

    public void execute(Long accountId, String targetUsername) {
        Profile blocker = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);
        Profile target = profileRepository.findByUsername(targetUsername)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);

        boolean deleted = profileRelationshipRepository.unblock(blocker.getId(), target.getId());
        if (!deleted) {
            throw ProfileError.NOT_BLOCKED.exception();
        }
        log.info("Unblock ok blockerId={}, targetId={}", blocker.getId(), target.getId());
    }
}
