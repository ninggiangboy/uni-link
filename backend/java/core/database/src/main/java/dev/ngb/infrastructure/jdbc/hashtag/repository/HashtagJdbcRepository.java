package dev.ngb.infrastructure.jdbc.hashtag.repository;

import dev.ngb.domain.hashtag.model.hashtag.Hashtag;
import dev.ngb.domain.hashtag.repository.HashtagRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.hashtag.entity.HashtagJdbcEntity;
import dev.ngb.infrastructure.jdbc.hashtag.mapper.HashtagJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class HashtagJdbcRepository extends JdbcRepository<Hashtag, HashtagJdbcEntity, Long> implements HashtagRepository {

    public HashtagJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(HashtagJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, HashtagJdbcMapper.INSTANCE);
    }
}
