package dev.ngb.infrastructure.jdbc.hashtag.repository;

import dev.ngb.domain.hashtag.model.analytics.HashtagRelation;
import dev.ngb.domain.hashtag.repository.HashtagRelationRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.hashtag.entity.HashtagRelationJdbcEntity;
import dev.ngb.infrastructure.jdbc.hashtag.mapper.HashtagRelationJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class HashtagRelationJdbcRepository extends JdbcRepository<HashtagRelation, HashtagRelationJdbcEntity, Long> implements HashtagRelationRepository {

    public HashtagRelationJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(HashtagRelationJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, HashtagRelationJdbcMapper.INSTANCE);
    }
}
