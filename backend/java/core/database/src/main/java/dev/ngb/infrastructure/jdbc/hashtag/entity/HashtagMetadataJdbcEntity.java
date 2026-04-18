package dev.ngb.infrastructure.jdbc.hashtag.entity;

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
@Table("ht_hashtag_metadata")
public class HashtagMetadataJdbcEntity extends JdbcEntity<Long> {

    private Long hashtagId;
    private String category;
    private String language;
    private String description;
}
