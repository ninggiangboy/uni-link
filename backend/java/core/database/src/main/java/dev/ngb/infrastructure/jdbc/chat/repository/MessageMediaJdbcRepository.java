package dev.ngb.infrastructure.jdbc.chat.repository;

import dev.ngb.domain.chat.model.message.MessageMedia;
import dev.ngb.domain.chat.repository.MessageMediaRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.chat.entity.MessageMediaJdbcEntity;
import dev.ngb.infrastructure.jdbc.chat.mapper.MessageMediaJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class MessageMediaJdbcRepository extends JdbcRepository<MessageMedia, MessageMediaJdbcEntity, Long>
        implements MessageMediaRepository {

    public MessageMediaJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(MessageMediaJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, MessageMediaJdbcMapper.INSTANCE);
    }
}
