package dev.ngb.infrastructure.jdbc.thread.repository;

import dev.ngb.domain.thread.model.thread.ThreadLinkPreview;
import dev.ngb.domain.thread.repository.ThreadLinkPreviewRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadLinkPreviewJdbcEntity;
import dev.ngb.infrastructure.jdbc.thread.mapper.ThreadLinkPreviewJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ThreadLinkPreviewJdbcRepository extends JdbcRepository<ThreadLinkPreview, ThreadLinkPreviewJdbcEntity, Long>
        implements ThreadLinkPreviewRepository {

    public ThreadLinkPreviewJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ThreadLinkPreviewJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ThreadLinkPreviewJdbcMapper.INSTANCE);
    }
}
