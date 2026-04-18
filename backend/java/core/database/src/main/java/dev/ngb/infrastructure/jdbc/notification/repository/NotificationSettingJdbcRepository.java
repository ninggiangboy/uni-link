package dev.ngb.infrastructure.jdbc.notification.repository;

import dev.ngb.domain.notification.model.profile.NotificationSetting;
import dev.ngb.domain.notification.repository.NotificationSettingRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.notification.entity.NotificationSettingJdbcEntity;
import dev.ngb.infrastructure.jdbc.notification.mapper.NotificationSettingJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationSettingJdbcRepository extends JdbcRepository<NotificationSetting, NotificationSettingJdbcEntity, Long> implements NotificationSettingRepository {

    public NotificationSettingJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(NotificationSettingJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, NotificationSettingJdbcMapper.INSTANCE);
    }
}
