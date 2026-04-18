package dev.ngb.infrastructure.jdbc.thread.repository;

import dev.ngb.domain.thread.model.interaction.ThreadRepost;
import dev.ngb.domain.thread.repository.ThreadRepostRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadRepostJdbcEntity;
import dev.ngb.infrastructure.jdbc.thread.mapper.ThreadRepostJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ThreadRepostJdbcRepository extends JdbcRepository<ThreadRepost, ThreadRepostJdbcEntity, Long> implements ThreadRepostRepository {

    public ThreadRepostJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ThreadRepostJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ThreadRepostJdbcMapper.INSTANCE);
    }
}
