package dev.ngb.infrastructure.jdbc.notification.repository;

import dev.ngb.domain.notification.model.notification.NotificationActor;
import dev.ngb.domain.notification.repository.NotificationActorRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.notification.entity.NotificationActorJdbcEntity;
import dev.ngb.infrastructure.jdbc.notification.mapper.NotificationActorJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationActorJdbcRepository extends JdbcRepository<NotificationActor, NotificationActorJdbcEntity, Long>
        implements NotificationActorRepository {

    public NotificationActorJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(NotificationActorJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, NotificationActorJdbcMapper.INSTANCE);
    }
}
