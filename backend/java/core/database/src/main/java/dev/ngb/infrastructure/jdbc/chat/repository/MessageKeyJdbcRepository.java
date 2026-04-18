package dev.ngb.infrastructure.jdbc.chat.repository;

import dev.ngb.domain.chat.model.message.MessageKey;
import dev.ngb.domain.chat.repository.MessageKeyRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.chat.entity.MessageKeyJdbcEntity;
import dev.ngb.infrastructure.jdbc.chat.mapper.MessageKeyJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class MessageKeyJdbcRepository extends JdbcRepository<MessageKey, MessageKeyJdbcEntity, Long> implements MessageKeyRepository {

    public MessageKeyJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(MessageKeyJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, MessageKeyJdbcMapper.INSTANCE);
    }
}
