package dev.ngb.infrastructure.jdbc.profile.entity;

import dev.ngb.domain.profile.model.relationship.FollowRequestStatus;
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
@Table("prf_follow_requests")
public class FollowRequestJdbcEntity extends JdbcEntity<Long> {

    private Long requesterProfileId;
    private Long targetProfileId;
    private FollowRequestStatus status;
    private Instant respondedAt;
}
