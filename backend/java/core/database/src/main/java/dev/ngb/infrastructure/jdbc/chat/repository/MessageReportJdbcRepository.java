package dev.ngb.infrastructure.jdbc.chat.repository;

import dev.ngb.domain.chat.model.moderation.MessageReport;
import dev.ngb.domain.chat.repository.MessageReportRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.chat.entity.MessageReportJdbcEntity;
import dev.ngb.infrastructure.jdbc.chat.mapper.MessageReportJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class MessageReportJdbcRepository extends JdbcRepository<MessageReport, MessageReportJdbcEntity, Long> implements MessageReportRepository {

    public MessageReportJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(MessageReportJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, MessageReportJdbcMapper.INSTANCE);
    }
}
