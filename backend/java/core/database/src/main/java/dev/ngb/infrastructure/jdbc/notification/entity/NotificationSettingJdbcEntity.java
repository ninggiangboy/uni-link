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
@Table("ntf_notification_settings")
public class NotificationSettingJdbcEntity extends JdbcEntity<Long> {

    private Long profileId;
    private Boolean likeEnabled;
    private Boolean replyEnabled;
    private Boolean mentionEnabled;
    private Boolean followEnabled;
    private Boolean repostEnabled;
    private Boolean quoteEnabled;
    private Boolean pushEnabled;
    private Boolean emailEnabled;
}
