package dev.ngb.infrastructure.jdbc.hashtag.repository;

import dev.ngb.domain.hashtag.model.analytics.HashtagUsageHourly;
import dev.ngb.domain.hashtag.repository.HashtagUsageHourlyRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.hashtag.entity.HashtagUsageHourlyJdbcEntity;
import dev.ngb.infrastructure.jdbc.hashtag.mapper.HashtagUsageHourlyJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class HashtagUsageHourlyJdbcRepository extends JdbcRepository<HashtagUsageHourly, HashtagUsageHourlyJdbcEntity, Long> implements HashtagUsageHourlyRepository {

    public HashtagUsageHourlyJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(HashtagUsageHourlyJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, HashtagUsageHourlyJdbcMapper.INSTANCE);
    }
}
