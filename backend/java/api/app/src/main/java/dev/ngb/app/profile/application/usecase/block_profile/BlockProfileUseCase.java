package dev.ngb.app.profile.application.usecase.block_profile;

import dev.ngb.app.profile.application.ProfileFollowStatsDeltaPublisher;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.repository.FollowRequestRepository;
import dev.ngb.domain.profile.repository.ProfileRelationshipRepository;
import dev.ngb.domain.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.time.Instant;

/*
 * Block flow:
 *   1. Check whether either party currently follows the other (BEFORE the BLOCK Cypher
 *      removes both follow edges atomically — see ProfileRelationshipCypher.BLOCK).
 *   2. Run the BLOCK Cypher; it MERGEs the BLOCKS edge and DELETEs both FOLLOWS edges.
 *   3. For every follow edge that existed pre-block, publish stats deltas for async decrements.
 *   4. Cancel any pending FollowRequest in either direction.
 *
 * The check-then-block window is acceptable because the BLOCK Cypher is the source of truth
 * for edge removal; if a FOLLOWS edge appears between step 1 and step 2 it will be removed
 * but the counters won't reflect it. In practice users don't follow + block within ms.
 */
@Slf4j
@RequiredArgsConstructor
public class BlockProfileUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final ProfileFollowStatsDeltaPublisher profileFollowStatsDeltaPublisher;
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

        boolean blockerFollowsTarget = profileRelationshipRepository.isFollowing(blocker.getId(), target.getId());
        boolean targetFollowsBlocker = profileRelationshipRepository.isFollowing(target.getId(), blocker.getId());

        boolean created = profileRelationshipRepository.block(
                blocker.getId(), target.getId(), Instant.now(Clock.systemUTC()));
        if (!created) {
            throw ProfileError.ALREADY_BLOCKED.exception();
        }

        if (blockerFollowsTarget) {
            profileFollowStatsDeltaPublisher.publish(target.getId(), -1, blocker.getId(), -1);
        }
        if (targetFollowsBlocker) {
            profileFollowStatsDeltaPublisher.publish(blocker.getId(), -1, target.getId(), -1);
        }

        cancelPending(blocker.getId(), target.getId());
        cancelPending(target.getId(), blocker.getId());

        log.info("Block created blockerId={}, targetId={}, removedFollows=[{}]",
                blocker.getId(), target.getId(),
                (blockerFollowsTarget ? "blocker→target " : "")
                        + (targetFollowsBlocker ? "target→blocker" : ""));
    }

    private void cancelPending(Long requesterId, Long targetId) {
        followRequestRepository.findPendingForPair(requesterId, targetId).ifPresent(req -> {
            req.cancel();
            followRequestRepository.save(req);
        });
    }
}
