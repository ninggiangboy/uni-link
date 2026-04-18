package dev.ngb.infrastructure.jdbc.thread.repository;

import dev.ngb.domain.thread.model.interaction.ThreadPollVote;
import dev.ngb.domain.thread.repository.ThreadPollVoteRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadPollVoteJdbcEntity;
import dev.ngb.infrastructure.jdbc.thread.mapper.ThreadPollVoteJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ThreadPollVoteJdbcRepository extends JdbcRepository<ThreadPollVote, ThreadPollVoteJdbcEntity, Long> implements ThreadPollVoteRepository {

    public ThreadPollVoteJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ThreadPollVoteJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ThreadPollVoteJdbcMapper.INSTANCE);
    }
}
