package dev.ngb.event;

import dev.ngb.constant.TopicNames;

import java.time.Instant;
import java.util.UUID;

/**
 * Published after the Neo4j FOLLOWS edge is created or removed so async workers and Streams can
 * reconcile follower/following counter deltas and run celeb promotion.
 */
@Topic(TopicNames.PROFILE_FOLLOW)
public record ProfileFollowStateEvent(
        String uuid,
        Instant occurredAt,
        State state,
        long targetProfileId,
        long followerProfileId,
        Boolean targetIsCeleb,
        Boolean statsAppliedInApi
) implements Event {

    public enum State {
        FOLLOW,
        UNFOLLOW
    }

    public static ProfileFollowStateEvent follow(long targetProfileId, long followerProfileId, boolean targetIsCeleb) {
        return new ProfileFollowStateEvent(
                UUID.randomUUID().toString(),
                Instant.now(),
                State.FOLLOW,
                targetProfileId,
                followerProfileId,
                targetIsCeleb,
                !targetIsCeleb
        );
    }

    public static ProfileFollowStateEvent unfollow(long targetProfileId, long followerProfileId, boolean targetIsCeleb) {
        return new ProfileFollowStateEvent(
                UUID.randomUUID().toString(),
                Instant.now(),
                State.UNFOLLOW,
                targetProfileId,
                followerProfileId,
                targetIsCeleb,
                !targetIsCeleb
        );
    }
}
