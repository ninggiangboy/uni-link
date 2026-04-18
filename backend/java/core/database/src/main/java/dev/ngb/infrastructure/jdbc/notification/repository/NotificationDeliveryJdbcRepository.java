package dev.ngb.infrastructure.jdbc.notification.repository;

import dev.ngb.domain.notification.model.notification.NotificationDelivery;
import dev.ngb.domain.notification.repository.NotificationDeliveryRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.notification.entity.NotificationDeliveryJdbcEntity;
import dev.ngb.infrastructure.jdbc.notification.mapper.NotificationDeliveryJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationDeliveryJdbcRepository extends JdbcRepository<NotificationDelivery, NotificationDeliveryJdbcEntity, Long>
        implements NotificationDeliveryRepository {

    public NotificationDeliveryJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(NotificationDeliveryJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, NotificationDeliveryJdbcMapper.INSTANCE);
    }
}
