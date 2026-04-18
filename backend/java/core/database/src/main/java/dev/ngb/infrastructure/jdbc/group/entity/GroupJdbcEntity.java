package dev.ngb.infrastructure.jdbc.group.entity;

import dev.ngb.domain.group.model.group.GroupStatus;
import dev.ngb.domain.group.model.group.GroupVisibility;
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
@Table("grp_groups")
public class GroupJdbcEntity extends SoftDeletableJdbcEntity<Long> {

    private Long ownerProfileId;
    private String name;
    private String slug;
    private String description;
    private String avatarUrl;
    private String bannerUrl;
    private GroupVisibility visibility;
    private GroupStatus status;
    private Boolean requiresApproval;
}
