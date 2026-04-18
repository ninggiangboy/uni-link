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
@Table("grp_group_rules")
public class GroupRuleJdbcEntity extends JdbcEntity<Long> {

    private Long groupId;
    private String title;
    private String description;
    private Integer position;
}
