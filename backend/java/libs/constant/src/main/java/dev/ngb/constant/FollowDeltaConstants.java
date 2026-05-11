package dev.ngb.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class FollowDeltaConstants {

    public static final String DELTA_KEY_PREFIX = "follow:delta:user:";

    public static final long FOLLOWER_DELTA_OFFSET = 1_000_000L;

    public static String deltaKey(long profileId) {
        return DELTA_KEY_PREFIX + profileId;
    }

    public static long parseProfileId(String key) {
        return Long.parseLong(key.substring(DELTA_KEY_PREFIX.length()));
    }

    public static long pack(int followerDelta, int followingDelta) {
        return (long) followerDelta + (long) followingDelta * FOLLOWER_DELTA_OFFSET;
    }

    public static long unpackFollower(long packed) {
        return packed % FOLLOWER_DELTA_OFFSET;
    }

    public static long unpackFollowing(long packed) {
        return packed / FOLLOWER_DELTA_OFFSET;
    }
}
