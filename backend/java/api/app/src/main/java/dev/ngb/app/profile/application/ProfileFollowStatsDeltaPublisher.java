package dev.ngb.app.profile.application;

import dev.ngb.application.ApplicationService;
import dev.ngb.application.port.event.EventPublisher;
import dev.ngb.event.ProfileFollowStatsDeltaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Best-effort publish of follow/unfollow counter deltas for async projection into {@code prf_profile_stats}.
 */
@Slf4j
@RequiredArgsConstructor
public class ProfileFollowStatsDeltaPublisher implements ApplicationService {

    private final EventPublisher eventPublisher;

    public void publish(long targetProfileId, int followerDelta, long followerProfileId, int followingDelta) {
        if (followerDelta == 0 && followingDelta == 0) {
            return;
        }
        try {
            eventPublisher.publish(ProfileFollowStatsDeltaEvent.create(
                    targetProfileId,
                    followerDelta,
                    followerProfileId,
                    followingDelta
            ));
        } catch (Exception ex) {
            log.error(
                    "Failed to publish profile follow stats delta targetProfileId={} followerDelta={} followerProfileId={} followingDelta={}",
                    targetProfileId,
                    followerDelta,
                    followerProfileId,
                    followingDelta,
                    ex
            );
        }
    }
}
