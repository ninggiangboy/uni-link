package dev.ngb.worker.profile.infrastrcuture;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;
import dev.ngb.worker.profile.application.port.ProfileStatsDeltaEventDedupePort;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class ProfileStatsDeltaEventDedupeRedisPort implements ProfileStatsDeltaEventDedupePort {

    private static final String KEY_PREFIX = "profile:follow-stats-delta:dedupe:";
    private static final Duration CLAIM_TTL = Duration.ofDays(1);

    private final RedissonClient redissonClient;

    @Override
    public boolean tryClaimEvent(String eventUuid) {
        if (eventUuid == null || eventUuid.isBlank()) {
            return false;
        }
        String key = KEY_PREFIX + eventUuid;
        return redissonClient.getBucket(key).setIfAbsent(Boolean.TRUE, CLAIM_TTL);
    }
}
