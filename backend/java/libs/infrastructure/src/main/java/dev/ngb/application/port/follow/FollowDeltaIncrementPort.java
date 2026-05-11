package dev.ngb.application.port.follow;

public interface FollowDeltaIncrementPort {

    void incrementDelta(long targetProfileId, int followerDelta, int followingDelta);

    long getFollowerDelta(long profileId);

    long getFollowingDelta(long profileId);
}
