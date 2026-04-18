package dev.ngb.infrastructure.jdbc.notification.entity;

import dev.ngb.domain.notification.model.notification.NotificationEntityType;
import dev.ngb.domain.notification.model.notification.NotificationType;
import dev.ngb.infrastructure.jdbc.base.entity.SoftDeletableJdbcEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table("ntf_notifications")
public class NotificationJdbcEntity extends SoftDeletableJdbcEntity<Long> {

    private Long recipientProfileId;
    private Long actorProfileId;
    private NotificationType type;
    private NotificationEntityType entityType;
    private Long entityId;
    private Boolean isRead;
    private String groupKey;
    private Long actorCount;
    private Long lastActorProfileId;
}
