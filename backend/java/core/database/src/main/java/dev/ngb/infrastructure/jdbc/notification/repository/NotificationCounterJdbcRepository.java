package dev.ngb.infrastructure.jdbc.notification.repository;

import dev.ngb.domain.notification.model.profile.NotificationCounter;
import dev.ngb.domain.notification.repository.NotificationCounterRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.notification.entity.NotificationCounterJdbcEntity;
import dev.ngb.infrastructure.jdbc.notification.mapper.NotificationCounterJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationCounterJdbcRepository extends JdbcRepository<NotificationCounter, NotificationCounterJdbcEntity, Long> implements NotificationCounterRepository {

    public NotificationCounterJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(NotificationCounterJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, NotificationCounterJdbcMapper.INSTANCE);
    }
}
