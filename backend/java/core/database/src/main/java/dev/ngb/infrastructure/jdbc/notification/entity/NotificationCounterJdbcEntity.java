package dev.ngb.infrastructure.jdbc.notification.entity;

import dev.ngb.infrastructure.jdbc.base.entity.JdbcEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table("ntf_notification_counters")
public class NotificationCounterJdbcEntity extends JdbcEntity<Long> {

    private Long profileId;
    private Long unreadCount;
}
