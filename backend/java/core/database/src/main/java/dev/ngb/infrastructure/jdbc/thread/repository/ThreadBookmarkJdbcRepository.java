package dev.ngb.infrastructure.jdbc.thread.repository;

import dev.ngb.domain.thread.model.interaction.ThreadBookmark;
import dev.ngb.domain.thread.repository.ThreadBookmarkRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadBookmarkJdbcEntity;
import dev.ngb.infrastructure.jdbc.thread.mapper.ThreadBookmarkJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ThreadBookmarkJdbcRepository extends JdbcRepository<ThreadBookmark, ThreadBookmarkJdbcEntity, Long> implements ThreadBookmarkRepository {

    public ThreadBookmarkJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ThreadBookmarkJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ThreadBookmarkJdbcMapper.INSTANCE);
    }
}
