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
@Table("ht_hashtag_relations")
public class HashtagRelationJdbcEntity extends JdbcEntity<Long> {

    private Long hashtagAId;
    private Long hashtagBId;
    private Long coOccurrenceCount;
}
