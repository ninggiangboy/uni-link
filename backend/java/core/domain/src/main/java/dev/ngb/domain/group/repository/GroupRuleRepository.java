package dev.ngb.domain.group.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.group.model.content.GroupRule;

import java.util.List;

/**
 * Repository for managing {@link GroupRule} entities.
 */
public interface GroupRuleRepository extends Repository<GroupRule, Long> {

    List<GroupRule> findByGroupId(Long groupId);
}
