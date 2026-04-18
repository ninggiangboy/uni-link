package dev.ngb.infrastructure.jdbc.thread.entity;

import dev.ngb.domain.thread.model.moderation.ThreadModerationStatus;
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
@Table("thr_thread_moderations")
public class ThreadModerationJdbcEntity extends JdbcEntity<Long> {

    private Long threadId;
    private Long reporterProfileId;
    private String reason;
    private ThreadModerationStatus status;
    private Long moderatorProfileId;
    private String resolution;
}
