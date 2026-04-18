package dev.ngb.infrastructure.jdbc.group.entity;

import dev.ngb.domain.group.model.membership.GroupMemberRequestStatus;
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
@Table("grp_group_member_requests")
public class GroupMemberRequestJdbcEntity extends JdbcEntity<Long> {

    private Long groupId;
    private Long requesterProfileId;
    private GroupMemberRequestStatus status;
    private Long reviewedByProfileId;
    private String reviewNote;
}
