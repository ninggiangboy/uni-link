package dev.ngb.infrastructure.jdbc.chat.repository;

import dev.ngb.domain.chat.model.moderation.ConversationBlock;
import dev.ngb.domain.chat.repository.ConversationBlockRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.chat.entity.ConversationBlockJdbcEntity;
import dev.ngb.infrastructure.jdbc.chat.mapper.ConversationBlockJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ConversationBlockJdbcRepository extends JdbcRepository<ConversationBlock, ConversationBlockJdbcEntity, Long> implements ConversationBlockRepository {

    public ConversationBlockJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ConversationBlockJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ConversationBlockJdbcMapper.INSTANCE);
    }
}
