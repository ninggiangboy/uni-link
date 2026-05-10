package dev.ngb.app.profile.application.usecase.unfollow_profile;

import dev.ngb.app.profile.application.service.FollowStatsSyncService;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.repository.FollowRequestRepository;
import dev.ngb.domain.profile.repository.ProfileRelationshipRepository;
import dev.ngb.domain.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Removes the FOLLOWS edge if it exists and publishes stats deltas for async counter decrements.
 * If the relationship is only a pending FollowRequest (private target), cancel it instead.
 */
@Slf4j
@RequiredArgsConstructor
public class UnfollowProfileUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final FollowStatsSyncService followStatsSyncService;
    private final ProfileRelationshipRepository profileRelationshipRepository;
    private final FollowRequestRepository followRequestRepository;

    public void execute(Long accountId, String targetUsername) {
        Profile follower = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);
        Profile target = profileRepository.findByUsername(targetUsername)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);

        boolean deleted = profileRelationshipRepository.unfollow(follower.getId(), target.getId());
        if (deleted) {
            followStatsSyncService.unfollow(target, follower);
            log.info("Unfollow ok followerId={}, targetId={}", follower.getId(), target.getId());
            return;
        }

        // Maybe a pending request — cancel it transparently.
        followRequestRepository.findPendingForPair(follower.getId(), target.getId()).ifPresentOrElse(
                request -> {
                    request.cancel();
                    followRequestRepository.save(request);
                    log.info("Unfollow cancelled pending request requesterId={}, targetId={}",
                            follower.getId(), target.getId());
                },
                () -> {
                    throw ProfileError.NOT_FOLLOWING.exception();
                }
        );
    }
}
