package dev.ngb.infrastructure.jdbc.group.entity;

import dev.ngb.domain.group.model.membership.GroupInvitationStatus;
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
@Table("grp_group_invitations")
public class GroupInvitationJdbcEntity extends JdbcEntity<Long> {

    private Long groupId;
    private Long inviterProfileId;
    private Long inviteeProfileId;
    private GroupInvitationStatus status;
    private String token;
    private Instant expiresAt;
}
