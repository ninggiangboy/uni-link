package dev.ngb.infrastructure.jdbc.hashtag.repository;

import dev.ngb.domain.hashtag.model.hashtag.HashtagModeration;
import dev.ngb.domain.hashtag.repository.HashtagModerationRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.hashtag.entity.HashtagModerationJdbcEntity;
import dev.ngb.infrastructure.jdbc.hashtag.mapper.HashtagModerationJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class HashtagModerationJdbcRepository extends JdbcRepository<HashtagModeration, HashtagModerationJdbcEntity, Long>
        implements HashtagModerationRepository {

    public HashtagModerationJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(HashtagModerationJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, HashtagModerationJdbcMapper.INSTANCE);
    }
}
