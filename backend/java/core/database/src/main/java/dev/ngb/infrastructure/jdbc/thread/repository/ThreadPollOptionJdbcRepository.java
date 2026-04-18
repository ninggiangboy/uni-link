package dev.ngb.infrastructure.jdbc.thread.repository;

import dev.ngb.domain.thread.model.thread.ThreadPollOption;
import dev.ngb.domain.thread.repository.ThreadPollOptionRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadPollOptionJdbcEntity;
import dev.ngb.infrastructure.jdbc.thread.mapper.ThreadPollOptionJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ThreadPollOptionJdbcRepository extends JdbcRepository<ThreadPollOption, ThreadPollOptionJdbcEntity, Long>
        implements ThreadPollOptionRepository {

    public ThreadPollOptionJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ThreadPollOptionJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ThreadPollOptionJdbcMapper.INSTANCE);
    }
}
