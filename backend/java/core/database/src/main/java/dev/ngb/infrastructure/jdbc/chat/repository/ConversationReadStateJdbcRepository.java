package dev.ngb.infrastructure.jdbc.chat.repository;

import dev.ngb.domain.chat.model.conversation.ConversationReadState;
import dev.ngb.domain.chat.repository.ConversationReadStateRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.chat.entity.ConversationReadStateJdbcEntity;
import dev.ngb.infrastructure.jdbc.chat.mapper.ConversationReadStateJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ConversationReadStateJdbcRepository extends JdbcRepository<ConversationReadState, ConversationReadStateJdbcEntity, Long>
        implements ConversationReadStateRepository {

    public ConversationReadStateJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ConversationReadStateJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ConversationReadStateJdbcMapper.INSTANCE);
    }
}
