package dev.ngb.infrastructure.jdbc.chat.repository;

import dev.ngb.domain.chat.model.message.Message;
import dev.ngb.domain.chat.repository.MessageRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.chat.entity.MessageJdbcEntity;
import dev.ngb.infrastructure.jdbc.chat.mapper.MessageJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class MessageJdbcRepository extends JdbcRepository<Message, MessageJdbcEntity, Long> implements MessageRepository {

    public MessageJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(MessageJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, MessageJdbcMapper.INSTANCE);
    }
}
