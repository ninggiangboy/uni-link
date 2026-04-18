package dev.ngb.infrastructure.jdbc.thread.repository;

import dev.ngb.domain.thread.model.thread.ThreadMedia;
import dev.ngb.domain.thread.repository.ThreadMediaRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadMediaJdbcEntity;
import dev.ngb.infrastructure.jdbc.thread.mapper.ThreadMediaJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ThreadMediaJdbcRepository extends JdbcRepository<ThreadMedia, ThreadMediaJdbcEntity, Long>
        implements ThreadMediaRepository {

    public ThreadMediaJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ThreadMediaJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ThreadMediaJdbcMapper.INSTANCE);
    }
}
