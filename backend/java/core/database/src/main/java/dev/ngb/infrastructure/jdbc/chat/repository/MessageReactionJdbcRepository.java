package dev.ngb.infrastructure.jdbc.chat.repository;

import dev.ngb.domain.chat.model.message.MessageReaction;
import dev.ngb.domain.chat.repository.MessageReactionRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.chat.entity.MessageReactionJdbcEntity;
import dev.ngb.infrastructure.jdbc.chat.mapper.MessageReactionJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class MessageReactionJdbcRepository extends JdbcRepository<MessageReaction, MessageReactionJdbcEntity, Long>
        implements MessageReactionRepository {

    public MessageReactionJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(MessageReactionJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, MessageReactionJdbcMapper.INSTANCE);
    }
}
