package dev.ngb.infrastructure.jdbc.hashtag.entity;

import dev.ngb.infrastructure.jdbc.base.entity.SoftDeletableJdbcEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table("ht_hashtags")
public class HashtagJdbcEntity extends SoftDeletableJdbcEntity<Long> {

    private String tag;
    private String normalizedTag;
    private Long usageCount;
    private Long threadCount;

    @Version
    private Long version;
}
