package dev.ngb.infrastructure.jdbc.group.entity;

import dev.ngb.domain.group.model.moderation.GroupModerationActionType;
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
@Table("grp_group_moderation_actions")
public class GroupModerationActionJdbcEntity extends JdbcEntity<Long> {

    private Long groupId;
    private Long targetProfileId;
    private Long moderatorProfileId;
    private GroupModerationActionType actionType;
    private String reason;
    private Instant expiresAt;
}
