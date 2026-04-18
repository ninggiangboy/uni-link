package dev.ngb.infrastructure.jdbc.profile.entity;

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
@Table("prf_profile_follows")
public class ProfileFollowJdbcEntity extends JdbcEntity<Long> {

    private Long followerProfileId;
    private Long followingProfileId;
}
