package dev.ngb.app.profile.application.usecase.block_profile;

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
 * Block flow:
 *   1. Check whether either party currently follows the other (BEFORE the BLOCK_AND_CLEANUP_FOLLOWS Cypher
 *      removes both follow edges atomically — see ProfileRelationshipCypher.BLOCK_AND_CLEANUP_FOLLOWS).
 *   2. Run the BLOCK_AND_CLEANUP_FOLLOWS Cypher; it MERGEs the BLOCKS edge and DELETEs both FOLLOWS edges.
 *   3. For every follow edge that existed pre-block, publish stats deltas for async decrements.
 *   4. Cancel any pending FollowRequest in either direction.
 *
 * The check-then-block window is acceptable because the BLOCK_AND_CLEANUP_FOLLOWS Cypher is the source of truth
 * for edge removal; if a FOLLOWS edge appears between step 1 and step 2 it will be removed
 * but the counters won't reflect it. In practice users don't follow + block within ms.
 */
@Slf4j
@RequiredArgsConstructor
public class BlockProfileUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final FollowStatsSyncService followStatsSyncService;
    private final ProfileRelationshipRepository profileRelationshipRepository;
    private final FollowRequestRepository followRequestRepository;

    public void execute(Long accountId, String targetUsername) {
        Profile blocker = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);
        Profile target = profileRepository.findByUsername(targetUsername)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);

        if (blocker.getId().equals(target.getId())) {
            throw ProfileError.CANNOT_BLOCK_SELF.exception();
        }

        var relState = profileRelationshipRepository.findRelationshipsBetween(blocker.getId(), target.getId());

        boolean created = profileRelationshipRepository.blockAndCleanupFollows(
                blocker.getId(), target.getId());
        if (!created) {
            throw ProfileError.ALREADY_BLOCKED.exception();
        }

        boolean sourceFollowed = relState.sourceFollowsTarget();
        boolean targetFollowed = relState.targetFollowsSource();

        if (sourceFollowed && targetFollowed) {
            followStatsSyncService.cleanUpFollow(target, blocker);
        } else {
            if (sourceFollowed) {
                followStatsSyncService.unfollow(target, blocker);
            }
            if (targetFollowed) {
                followStatsSyncService.unfollow(blocker, target);
            }
        }

        cancelPending(blocker.getId(), target.getId());
        cancelPending(target.getId(), blocker.getId());

        log.info("Block created blockerId={}, targetId={}, removedFollows=[{}]",
                blocker.getId(), target.getId(),
                (relState.sourceFollowsTarget() ? "blocker→target " : "")
                        + (relState.targetFollowsSource() ? "target→blocker" : ""));
    }

    private void cancelPending(Long requesterId, Long targetId) {
        followRequestRepository.findPendingForPair(requesterId, targetId).ifPresent(req -> {
            req.cancel();
            followRequestRepository.save(req);
        });
    }
}
