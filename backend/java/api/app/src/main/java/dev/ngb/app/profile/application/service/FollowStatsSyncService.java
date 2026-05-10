package dev.ngb.app.profile.application.service;

import dev.ngb.application.ApplicationService;
import dev.ngb.application.port.event.EventPublisher;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.stats.FollowCountChange;
import dev.ngb.domain.profile.repository.ProfileStatsRepository;
import dev.ngb.event.ProfileFollowStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Follow/unfollow counter updates: applies {@code prf_profile_stats} synchronously for all targets
 * and always publishes {@link ProfileFollowStateEvent} for Kafka Streams.
 * <p>
 * All events carry {@code statsAppliedInApi=true} so the worker batch consumer skips counter
 * adjustments and only runs celeb promotion from DB follower counts.
 * <p>
 * Event publishing can be skipped entirely when Kafka Streams is disabled
 */
@Slf4j
public class FollowStatsSyncService implements ApplicationService {

    private final EventPublisher eventPublisher;
    private final ProfileStatsRepository profileStatsRepository;

    public FollowStatsSyncService(
            EventPublisher eventPublisher,
            ProfileStatsRepository profileStatsRepository
    ) {
        this.eventPublisher = eventPublisher;
        this.profileStatsRepository = profileStatsRepository;
    }

    public void follow(Profile target, Profile follower) {
        apply(target, follower, 1);
    }

    public void unfollow(Profile target, Profile follower) {
        apply(target, follower, -1);
    }

    public void cleanUpFollow(Profile a, Profile b) {
        boolean aIsCeleb = a.getIsCeleb();
        boolean bIsCeleb = b.getIsCeleb();
        List<FollowCountChange> changes = new ArrayList<>(4);
        if (!aIsCeleb) {
            changes.add(FollowCountChange.decreaseFollowerCount(a.getId()));
            changes.add(FollowCountChange.decreaseFollowingCount(b.getId()));
        }
        if (!bIsCeleb) {
            changes.add(FollowCountChange.decreaseFollowerCount(b.getId()));
            changes.add(FollowCountChange.decreaseFollowingCount(a.getId()));
        }
        if (!changes.isEmpty()) {
            profileStatsRepository.adjustCountsBulk(changes);
        }
        eventPublisher.publish(ProfileFollowStateEvent.unfollow(a.getId(), b.getId(), aIsCeleb));
        eventPublisher.publish(ProfileFollowStateEvent.unfollow(b.getId(), a.getId(), bIsCeleb));
    }

    private void apply(Profile target, Profile follower, int delta) {
        boolean targetIsCeleb = target.getIsCeleb();
        List<FollowCountChange> changes = new ArrayList<>(2);
        if (delta > 0) {
            changes.add(FollowCountChange.increaseFollowerCount(target.getId()));
            changes.add(FollowCountChange.increaseFollowingCount(follower.getId()));
        } else {
            changes.add(FollowCountChange.decreaseFollowerCount(target.getId()));
            changes.add(FollowCountChange.decreaseFollowingCount(follower.getId()));
        }
        if (!targetIsCeleb) {
            profileStatsRepository.adjustCountsBulk(changes);
        }
        var event = delta > 0
                ? ProfileFollowStateEvent.follow(target.getId(), follower.getId(), targetIsCeleb)
                : ProfileFollowStateEvent.unfollow(target.getId(), follower.getId(), targetIsCeleb);
        eventPublisher.publish(event);
    }
}
