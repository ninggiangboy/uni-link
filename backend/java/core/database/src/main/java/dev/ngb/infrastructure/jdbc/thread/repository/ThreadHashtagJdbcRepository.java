package dev.ngb.infrastructure.jdbc.thread.repository;

import dev.ngb.domain.thread.model.thread.ThreadHashtag;
import dev.ngb.domain.thread.repository.ThreadHashtagRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadHashtagJdbcEntity;
import dev.ngb.infrastructure.jdbc.thread.mapper.ThreadHashtagJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ThreadHashtagJdbcRepository extends JdbcRepository<ThreadHashtag, ThreadHashtagJdbcEntity, Long>
        implements ThreadHashtagRepository {

    public ThreadHashtagJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ThreadHashtagJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ThreadHashtagJdbcMapper.INSTANCE);
    }
}
