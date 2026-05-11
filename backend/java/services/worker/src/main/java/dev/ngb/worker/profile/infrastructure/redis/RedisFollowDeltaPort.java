package dev.ngb.worker.profile.infrastructure.redis;

import dev.ngb.application.port.follow.FollowDeltaFlushPort;
import dev.ngb.constant.FollowDeltaConstants;
import dev.ngb.domain.profile.model.stats.FollowCountChange;
import dev.ngb.infrastructure.redis.RedisClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class RedisFollowDeltaPort implements FollowDeltaFlushPort {

    private final RedisClient redisClient;

    public RedisFollowDeltaPort(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    @Override
    public List<FollowCountChange> getAllAndReset() {
        Set<String> keys = redisClient.scan(FollowDeltaConstants.DELTA_KEY_PREFIX + "*", 100);
        if (keys.isEmpty()) {
            return List.of();
        }

        List<FollowCountChange> changes = new ArrayList<>();
        for (String key : keys) {
            Long packed = redisClient.get(key);
            if (packed == null || packed == 0L) {
                continue;
            }
            long profileId = FollowDeltaConstants.parseProfileId(key);
            int followerDelta = (int) FollowDeltaConstants.unpackFollower(packed);
            int followingDelta = (int) FollowDeltaConstants.unpackFollowing(packed);
            changes.add(new FollowCountChange(profileId, followerDelta, followingDelta));
            redisClient.delete(key);
        }
        return changes;
    }
}
