package dev.ngb.app.profile.infrastructure.redis;

import dev.ngb.infrastructure.redis.RedisClient;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class FollowRateCounter {

    private static final String RATE_KEY_PREFIX = "follow:rate:user:";
    private static final String HOT_KEY_PREFIX = "follow:hot:user:";
    private static final long RATE_WINDOW_SECONDS = 5;
    private static final long RATE_THRESHOLD = 1000;
    private static final Duration HOT_TTL = Duration.ofMinutes(30);

    private final RedisClient redisClient;

    public FollowRateCounter(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public boolean isHot(long targetUserId) {
        String rateKey = RATE_KEY_PREFIX + targetUserId;
        long count = redisClient.increment(rateKey);
        if (count == 1) {
            redisClient.expire(rateKey, Duration.ofSeconds(RATE_WINDOW_SECONDS));
        }
        if (count >= RATE_THRESHOLD) {
            String hotKey = HOT_KEY_PREFIX + targetUserId;
            redisClient.set(hotKey, true, HOT_TTL);
            return true;
        }
        String hotKey = HOT_KEY_PREFIX + targetUserId;
        Boolean hot = redisClient.get(hotKey);
        return Boolean.TRUE.equals(hot);
    }
}
