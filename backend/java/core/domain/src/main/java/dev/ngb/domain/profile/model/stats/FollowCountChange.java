package dev.ngb.domain.profile.model.stats;

public record FollowCountChange(long profileId, long followerDelta, long followingDelta) {

    public static FollowCountChange increaseFollowerCount(long profileId) {
        return new FollowCountChange(profileId, 1, 0L);
    }

    public static FollowCountChange decreaseFollowerCount(long profileId) {
        return new FollowCountChange(profileId, -1, 0L);
    }

    public static FollowCountChange increaseFollowingCount(long profileId) {
        return new FollowCountChange(profileId, 0L, 1);
    }

    public static FollowCountChange decreaseFollowingCount(long profileId) {
        return new FollowCountChange(profileId, 0L, -1);
    }
}
