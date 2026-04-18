package dev.ngb.infrastructure.jdbc.thread.repository;

import dev.ngb.domain.thread.model.thread.Thread;
import dev.ngb.domain.thread.repository.ThreadRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadJdbcEntity;
import dev.ngb.infrastructure.jdbc.thread.mapper.ThreadJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ThreadJdbcRepository extends JdbcRepository<Thread, ThreadJdbcEntity, Long> implements ThreadRepository {

    public ThreadJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ThreadJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ThreadJdbcMapper.INSTANCE);
    }
}
