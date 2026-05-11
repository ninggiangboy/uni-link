package dev.ngb.app.profile.infrastructure.redis;

import dev.ngb.application.port.follow.FollowDeltaIncrementPort;
import dev.ngb.constant.FollowDeltaConstants;
import dev.ngb.infrastructure.redis.RedisClient;
import org.springframework.stereotype.Component;

@Component
public class FollowDeltaAggregator implements FollowDeltaIncrementPort {

    private final RedisClient redisClient;

    public FollowDeltaAggregator(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    @Override
    public void incrementDelta(long targetProfileId, int followerDelta, int followingDelta) {
        String key = FollowDeltaConstants.deltaKey(targetProfileId);
        long packed = FollowDeltaConstants.pack(followerDelta, followingDelta);
        redisClient.increment(key, packed);
    }

    @Override
    public long getFollowerDelta(long profileId) {
        String key = FollowDeltaConstants.deltaKey(profileId);
        Long value = redisClient.get(key);
        if (value == null) {
            return 0L;
        }
        return FollowDeltaConstants.unpackFollower(value);
    }

    @Override
    public long getFollowingDelta(long profileId) {
        String key = FollowDeltaConstants.deltaKey(profileId);
        Long value = redisClient.get(key);
        if (value == null) {
            return 0L;
        }
        return FollowDeltaConstants.unpackFollowing(value);
    }
}
