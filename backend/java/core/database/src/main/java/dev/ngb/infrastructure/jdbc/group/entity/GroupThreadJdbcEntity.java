package dev.ngb.infrastructure.jdbc.group.entity;

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
@Table("grp_group_threads")
public class GroupThreadJdbcEntity extends JdbcEntity<Long> {

    private Long groupId;
    private Long threadId;
    private Boolean isPinned;
}
