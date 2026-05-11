package dev.ngb.app.profile.application.service;

import dev.ngb.app.profile.infrastructure.redis.FollowRateCounter;
import dev.ngb.application.ApplicationService;
import dev.ngb.application.port.follow.FollowDeltaIncrementPort;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.stats.FollowCountChange;
import dev.ngb.domain.profile.repository.ProfileStatsRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptive follow/unfollow counter updates:
 * <ul>
 *   <li>Following count → always sync DB update (low frequency per user)</li>
 *   <li>Follower count → sync DB for normal users, Redis delta for hot users</li>
 * </ul>
 */
@Slf4j
public class FollowStatsSyncService implements ApplicationService {

    private final ProfileStatsRepository profileStatsRepository;
    private final FollowRateCounter followRateCounter;
    private final FollowDeltaIncrementPort followDeltaIncrementPort;

    public FollowStatsSyncService(
            ProfileStatsRepository profileStatsRepository,
            FollowRateCounter followRateCounter,
            FollowDeltaIncrementPort followDeltaIncrementPort
    ) {
        this.profileStatsRepository = profileStatsRepository;
        this.followRateCounter = followRateCounter;
        this.followDeltaIncrementPort = followDeltaIncrementPort;
    }

    public void follow(Profile target, Profile follower) {
        apply(target, follower, 1);
    }

    public void unfollow(Profile target, Profile follower) {
        apply(target, follower, -1);
    }

    public void cleanUpFollow(Profile a, Profile b) {
        apply(a, b, -1);
        apply(b, a, -1);
    }

    private void apply(Profile target, Profile follower, int delta) {
        boolean increase = delta > 0;
        boolean targetHot = followRateCounter.isHot(target.getId());

        List<FollowCountChange> changes = new ArrayList<>(2);

        if (targetHot) {
            followDeltaIncrementPort.incrementDelta(target.getId(), delta, 0);
        } else {
            changes.add(increase
                    ? FollowCountChange.increaseFollowerCount(target.getId())
                    : FollowCountChange.decreaseFollowerCount(target.getId()));
        }

        changes.add(increase
                ? FollowCountChange.increaseFollowingCount(follower.getId())
                : FollowCountChange.decreaseFollowingCount(follower.getId()));

        profileStatsRepository.adjustCountsBulk(changes);
    }
}
