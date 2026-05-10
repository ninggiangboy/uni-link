package dev.ngb.app.profile.application.usecase.social.remove_follower;

import dev.ngb.app.profile.application.service.FollowStatsSyncService;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.repository.ProfileRelationshipRepository;
import dev.ngb.domain.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Inverse of unfollow: the profile owner removes someone who was following them.
 * Deletes the FOLLOWS edge from follower → owner and publishes stats deltas.
 */
@Slf4j
@RequiredArgsConstructor
public class RemoveFollowerUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final FollowStatsSyncService followStatsSyncService;
    private final ProfileRelationshipRepository profileRelationshipRepository;

    public void execute(Long accountId, String followerUsername) {
        Profile owner = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);
        Profile follower = profileRepository.findByUsername(followerUsername)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);

        boolean deleted = profileRelationshipRepository.unfollow(follower.getId(), owner.getId());
        if (!deleted) {
            throw ProfileError.NOT_FOLLOWED_BY.exception();
        }

        followStatsSyncService.unfollow(owner, follower);
        log.info("Follower removed ownerId={}, followerId={}", owner.getId(), follower.getId());
    }
}
