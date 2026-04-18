package dev.ngb.infrastructure.jdbc.thread.repository;

import dev.ngb.domain.thread.model.thread.ThreadPoll;
import dev.ngb.domain.thread.repository.ThreadPollRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadPollJdbcEntity;
import dev.ngb.infrastructure.jdbc.thread.mapper.ThreadPollJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ThreadPollJdbcRepository extends JdbcRepository<ThreadPoll, ThreadPollJdbcEntity, Long>
        implements ThreadPollRepository {

    public ThreadPollJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ThreadPollJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ThreadPollJdbcMapper.INSTANCE);
    }
}
