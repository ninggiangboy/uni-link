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
@Table("prf_profile_metadata")
public class ProfileMetadataJdbcEntity extends JdbcEntity<Long> {

    private Long profileId;
    private String key;
    private String value;
}
