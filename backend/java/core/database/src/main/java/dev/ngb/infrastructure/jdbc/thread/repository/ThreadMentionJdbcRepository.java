package dev.ngb.infrastructure.jdbc.thread.repository;

import dev.ngb.domain.thread.model.thread.ThreadMention;
import dev.ngb.domain.thread.repository.ThreadMentionRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadMentionJdbcEntity;
import dev.ngb.infrastructure.jdbc.thread.mapper.ThreadMentionJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ThreadMentionJdbcRepository extends JdbcRepository<ThreadMention, ThreadMentionJdbcEntity, Long>
        implements ThreadMentionRepository {

    public ThreadMentionJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ThreadMentionJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ThreadMentionJdbcMapper.INSTANCE);
    }
}
