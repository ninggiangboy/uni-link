package dev.ngb.app.profile.application.usecase.social.follow_profile;

import dev.ngb.app.profile.application.service.FollowStatsSyncService;
import dev.ngb.app.profile.application.usecase.social.follow_profile.dto.FollowResponse;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.relationship.FollowRequest;
import dev.ngb.domain.profile.model.relationship.ProfileRelationshipState;
import dev.ngb.domain.profile.repository.FollowRequestRepository;
import dev.ngb.domain.profile.repository.ProfileRelationshipRepository;
import dev.ngb.domain.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Follow flow:
 *   - Resolves both the follower (via accountId → Profile) and the target (via username).
 *   - Rejects self-follow, follow of HIDDEN profile, and follow when either party blocks the other.
 *   - PUBLIC target: creates the FOLLOWS edge in Neo4j; if a new edge was created, publishes a stats
 *     delta event so workers can update denormalized counters in Postgres.
 *   - PRIVATE target: skips the edge and persists a PENDING FollowRequest. Caller approves later.
 */
@Slf4j
@RequiredArgsConstructor
public class FollowProfileUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final FollowStatsSyncService followStatsSyncService;
    private final ProfileRelationshipRepository profileRelationshipRepository;
    private final FollowRequestRepository followRequestRepository;

    public FollowResponse execute(Long accountId, String targetUsername) {
        Profile follower = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);
        Profile target = profileRepository.findByUsername(targetUsername)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);

        if (follower.getId().equals(target.getId())) {
            throw ProfileError.CANNOT_FOLLOW_SELF.exception();
        }
        if (target.isHidden()) {
            // HIDDEN profiles are invisible to everyone but the owner.
            throw ProfileError.PROFILE_NOT_FOUND.exception();
        }
        ProfileRelationshipState relState = profileRelationshipRepository
                .findRelationshipsBetween(follower.getId(), target.getId());

        if (relState.targetBlocksSource()) {
            throw ProfileError.BLOCKED_BY_TARGET.exception();
        }
        if (relState.sourceBlocksTarget()) {
            throw ProfileError.TARGET_BLOCKED.exception();
        }

        if (target.isPrivate()) {
            return createPendingRequest(follower, target, relState);
        }
        return createDirectFollow(follower, target);
    }

    private FollowResponse createDirectFollow(Profile follower, Profile target) {
        boolean created = profileRelationshipRepository.follow(
                follower.getId(), target.getId());
        if (!created) {
            throw ProfileError.ALREADY_FOLLOWING.exception();
        }
        followStatsSyncService.follow(target, follower);
        log.info("Follow created followerId={}, targetId={}", follower.getId(), target.getId());
        return FollowResponse.following();
    }

    private FollowResponse createPendingRequest(Profile follower, Profile target, ProfileRelationshipState relState) {
        if (relState.sourceFollowsTarget()) {
            throw ProfileError.ALREADY_FOLLOWING.exception();
        }
        if (followRequestRepository.existsPending(follower.getId(), target.getId())) {
            throw ProfileError.FOLLOW_REQUEST_ALREADY_PENDING.exception();
        }
        FollowRequest pending = followRequestRepository.save(
                FollowRequest.createPending(follower.getId(), target.getId()));
        log.info("Follow request pending requesterId={}, targetId={}, requestId={}",
                follower.getId(), target.getId(), pending.getId());
        return FollowResponse.requested(pending.getUuid());
    }
}
