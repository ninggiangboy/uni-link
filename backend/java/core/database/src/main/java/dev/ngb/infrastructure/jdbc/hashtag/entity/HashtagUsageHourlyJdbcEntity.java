package dev.ngb.infrastructure.jdbc.hashtag.entity;

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
@Table("ht_hashtag_usage_hourly")
public class HashtagUsageHourlyJdbcEntity extends JdbcEntity<Long> {

    private Long hashtagId;
    private Instant hourBucket;
    private Long count;
}
