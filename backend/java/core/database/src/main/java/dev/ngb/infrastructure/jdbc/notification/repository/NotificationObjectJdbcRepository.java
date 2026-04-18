package dev.ngb.infrastructure.jdbc.notification.repository;

import dev.ngb.domain.notification.model.notification.NotificationObject;
import dev.ngb.domain.notification.repository.NotificationObjectRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.notification.entity.NotificationObjectJdbcEntity;
import dev.ngb.infrastructure.jdbc.notification.mapper.NotificationObjectJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationObjectJdbcRepository extends JdbcRepository<NotificationObject, NotificationObjectJdbcEntity, Long>
        implements NotificationObjectRepository {

    public NotificationObjectJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(NotificationObjectJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, NotificationObjectJdbcMapper.INSTANCE);
    }
}
