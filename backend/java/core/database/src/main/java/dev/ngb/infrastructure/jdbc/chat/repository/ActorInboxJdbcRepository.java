package dev.ngb.infrastructure.jdbc.chat.repository;

import dev.ngb.domain.chat.model.inbox.ActorInbox;
import dev.ngb.domain.chat.repository.ActorInboxRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.chat.entity.ActorInboxJdbcEntity;
import dev.ngb.infrastructure.jdbc.chat.mapper.ActorInboxJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ActorInboxJdbcRepository extends JdbcRepository<ActorInbox, ActorInboxJdbcEntity, Long> implements ActorInboxRepository {

    public ActorInboxJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ActorInboxJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ActorInboxJdbcMapper.INSTANCE);
    }
}
