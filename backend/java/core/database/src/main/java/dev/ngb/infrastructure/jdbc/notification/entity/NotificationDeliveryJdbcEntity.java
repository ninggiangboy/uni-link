package dev.ngb.infrastructure.jdbc.notification.entity;

import dev.ngb.domain.notification.model.notification.NotificationChannel;
import dev.ngb.domain.notification.model.notification.NotificationDeliveryStatus;
import dev.ngb.infrastructure.jdbc.base.entity.JdbcEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table("ntf_notification_deliveries")
public class NotificationDeliveryJdbcEntity extends JdbcEntity<Long> {

    private Long notificationId;
    private NotificationChannel channel;
    private NotificationDeliveryStatus status;
    private Instant sentAt;
}
