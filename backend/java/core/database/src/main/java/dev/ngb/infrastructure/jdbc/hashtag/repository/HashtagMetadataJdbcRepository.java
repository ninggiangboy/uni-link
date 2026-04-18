package dev.ngb.infrastructure.jdbc.hashtag.repository;

import dev.ngb.domain.hashtag.model.hashtag.HashtagMetadata;
import dev.ngb.domain.hashtag.repository.HashtagMetadataRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.hashtag.entity.HashtagMetadataJdbcEntity;
import dev.ngb.infrastructure.jdbc.hashtag.mapper.HashtagMetadataJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class HashtagMetadataJdbcRepository extends JdbcRepository<HashtagMetadata, HashtagMetadataJdbcEntity, Long>
        implements HashtagMetadataRepository {

    public HashtagMetadataJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(HashtagMetadataJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, HashtagMetadataJdbcMapper.INSTANCE);
    }
}
