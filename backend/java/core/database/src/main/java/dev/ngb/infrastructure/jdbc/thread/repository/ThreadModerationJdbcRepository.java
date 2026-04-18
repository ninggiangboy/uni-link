package dev.ngb.infrastructure.jdbc.thread.repository;

import dev.ngb.domain.thread.model.moderation.ThreadModeration;
import dev.ngb.domain.thread.repository.ThreadModerationRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadModerationJdbcEntity;
import dev.ngb.infrastructure.jdbc.thread.mapper.ThreadModerationJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ThreadModerationJdbcRepository extends JdbcRepository<ThreadModeration, ThreadModerationJdbcEntity, Long> implements ThreadModerationRepository {

    public ThreadModerationJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ThreadModerationJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ThreadModerationJdbcMapper.INSTANCE);
    }
}
