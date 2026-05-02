package dev.ngb.event;

import dev.ngb.constant.TopicNames;

import java.time.Instant;
import java.util.UUID;

/**
 * Published after the Neo4j FOLLOWS edge is created or removed so async workers can batch-update
 * {@code prf_profile_stats} follower/following counters.
 */
@Topic(TopicNames.PROFILE_FOLLOW_STATS_DELTA)
public record ProfileFollowStatsDeltaEvent(
        String uuid,
        Instant occurredAt,
        long targetProfileId,
        int followerDelta,
        long followerProfileId,
        int followingDelta
) implements Event {

    public static ProfileFollowStatsDeltaEvent create(
            long targetProfileId,
            int followerDelta,
            long followerProfileId,
            int followingDelta
    ) {
        return new ProfileFollowStatsDeltaEvent(
                UUID.randomUUID().toString(),
                Instant.now(),
                targetProfileId,
                followerDelta,
                followerProfileId,
                followingDelta
        );
    }
}
