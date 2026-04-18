package dev.ngb.infrastructure.jdbc.hashtag.repository;

import dev.ngb.domain.hashtag.model.hashtag.HashtagStats;
import dev.ngb.domain.hashtag.repository.HashtagStatsRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.hashtag.entity.HashtagStatsJdbcEntity;
import dev.ngb.infrastructure.jdbc.hashtag.mapper.HashtagStatsJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class HashtagStatsJdbcRepository extends JdbcRepository<HashtagStats, HashtagStatsJdbcEntity, Long>
        implements HashtagStatsRepository {

    public HashtagStatsJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(HashtagStatsJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, HashtagStatsJdbcMapper.INSTANCE);
    }
}
