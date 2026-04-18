package dev.ngb.infrastructure.jdbc.thread.repository;

import dev.ngb.domain.thread.model.interaction.ThreadLike;
import dev.ngb.domain.thread.repository.ThreadLikeRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadLikeJdbcEntity;
import dev.ngb.infrastructure.jdbc.thread.mapper.ThreadLikeJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ThreadLikeJdbcRepository extends JdbcRepository<ThreadLike, ThreadLikeJdbcEntity, Long> implements ThreadLikeRepository {

    public ThreadLikeJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ThreadLikeJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ThreadLikeJdbcMapper.INSTANCE);
    }
}
