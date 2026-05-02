package dev.ngb.infrastructure.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Standalone Redisson client for components that need Redisson APIs (e.g. {@link org.redisson.api.RBloomFilter}).
 * Disabled unless {@code ngb.redis.redisson.enabled=true} so services without Redis at startup still run.
 */
@Configuration
@ConditionalOnProperty(name = "ngb.redis.redisson.enabled", havingValue = "true")
@ConditionalOnMissingBean(RedissonClient.class)
public class RedissonClientConfiguration {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(
            @Value("${spring.data.redis.host:localhost}") String host,
            @Value("${spring.data.redis.port:6379}") int port,
            @Value("${spring.data.redis.password:}") String password
    ) {
        Config config = new Config();
        String address = "redis://" + host + ":" + port;
        var server = config.useSingleServer().setAddress(address);
        if (StringUtils.hasText(password)) {
            server.setPassword(password);
        }
        return Redisson.create(config);
    }
}
