package dev.ngb.infrastructure.jdbc.chat.repository;

import dev.ngb.domain.chat.model.conversation.ConversationParticipant;
import dev.ngb.domain.chat.repository.ConversationParticipantRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.chat.entity.ConversationParticipantJdbcEntity;
import dev.ngb.infrastructure.jdbc.chat.mapper.ConversationParticipantJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ConversationParticipantJdbcRepository extends JdbcRepository<ConversationParticipant, ConversationParticipantJdbcEntity, Long>
        implements ConversationParticipantRepository {

    public ConversationParticipantJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ConversationParticipantJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ConversationParticipantJdbcMapper.INSTANCE);
    }
}
