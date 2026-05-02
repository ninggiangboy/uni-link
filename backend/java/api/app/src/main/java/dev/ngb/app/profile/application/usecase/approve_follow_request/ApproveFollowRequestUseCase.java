package dev.ngb.app.profile.application.usecase.approve_follow_request;

import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.relationship.FollowRequest;
import dev.ngb.domain.profile.model.stats.ProfileStats;
import dev.ngb.domain.profile.repository.FollowRequestRepository;
import dev.ngb.domain.profile.repository.ProfileRelationshipRepository;
import dev.ngb.domain.profile.repository.ProfileRepository;
import dev.ngb.domain.profile.repository.ProfileStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.time.Instant;

/*
 * Approves a pending FollowRequest:
 *   1. Authorises only the request's target profile.
 *   2. Marks the request APPROVED.
 *   3. Creates the FOLLOWS edge in Neo4j (idempotent — Cypher MERGE).
 *   4. Increments stats only when a new edge was actually created.
 */
@Slf4j
@RequiredArgsConstructor
public class ApproveFollowRequestUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final ProfileStatsRepository profileStatsRepository;
    private final ProfileRelationshipRepository profileRelationshipRepository;
    private final FollowRequestRepository followRequestRepository;

    public void execute(Long accountId, String requestUuid) {
        Profile owner = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);

        FollowRequest request = followRequestRepository.findByUuidAndTargetProfileId(requestUuid, owner.getId())
                .orElseThrow(ProfileError.FOLLOW_REQUEST_NOT_FOUND::exception);

        if (!request.isPending()) {
            throw ProfileError.FOLLOW_REQUEST_NOT_FOUND.exception();
        }

        request.approve();
        followRequestRepository.save(request);

        boolean created = profileRelationshipRepository.follow(
                request.getRequesterProfileId(), owner.getId(), Instant.now(Clock.systemUTC()));
        if (created) {
            ProfileStats targetStats = profileStatsRepository.findByProfileId(owner.getId())
                    .orElseGet(() -> ProfileStats.createForNewProfile(owner.getId()));
            targetStats.incrementFollower();
            profileStatsRepository.save(targetStats);

            ProfileStats requesterStats = profileStatsRepository.findByProfileId(request.getRequesterProfileId())
                    .orElseGet(() -> ProfileStats.createForNewProfile(request.getRequesterProfileId()));
            requesterStats.incrementFollowing();
            profileStatsRepository.save(requesterStats);
        }
        log.info("Follow request approved requestId={}, requesterId={}, ownerId={}, edgeCreated={}",
                request.getId(), request.getRequesterProfileId(), owner.getId(), created);
    }
}
