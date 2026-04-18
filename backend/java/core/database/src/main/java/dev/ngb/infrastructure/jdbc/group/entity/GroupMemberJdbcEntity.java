package dev.ngb.infrastructure.jdbc.group.entity;

import dev.ngb.domain.group.model.member.GroupMemberRole;
import dev.ngb.domain.group.model.member.GroupMemberStatus;
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
@Table("grp_group_members")
public class GroupMemberJdbcEntity extends JdbcEntity<Long> {

    private Long groupId;
    private Long profileId;
    private GroupMemberRole role;
    private GroupMemberStatus status;
    private Instant mutedUntil;
    private Long permissions;
}
