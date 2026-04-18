package dev.ngb.infrastructure.jdbc.notification.repository;

import dev.ngb.domain.notification.model.notification.Notification;
import dev.ngb.domain.notification.repository.NotificationRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.notification.entity.NotificationJdbcEntity;
import dev.ngb.infrastructure.jdbc.notification.mapper.NotificationJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationJdbcRepository extends JdbcRepository<Notification, NotificationJdbcEntity, Long> implements NotificationRepository {

    public NotificationJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(NotificationJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, NotificationJdbcMapper.INSTANCE);
    }
}
