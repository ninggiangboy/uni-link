package dev.ngb.infrastructure.jdbc.chat.repository;

import dev.ngb.domain.chat.model.conversation.Conversation;
import dev.ngb.domain.chat.repository.ConversationRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.chat.entity.ConversationJdbcEntity;
import dev.ngb.infrastructure.jdbc.chat.mapper.ConversationJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ConversationJdbcRepository extends JdbcRepository<Conversation, ConversationJdbcEntity, Long> implements ConversationRepository {

    public ConversationJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ConversationJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ConversationJdbcMapper.INSTANCE);
    }
}
