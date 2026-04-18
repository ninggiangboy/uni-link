package dev.ngb.infrastructure.jdbc.hashtag.entity;

import dev.ngb.domain.hashtag.model.hashtag.HashtagModerationStatus;
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
@Table("ht_hashtag_moderations")
public class HashtagModerationJdbcEntity extends JdbcEntity<Long> {

    private Long hashtagId;
    private HashtagModerationStatus status;
    private String reason;
}
