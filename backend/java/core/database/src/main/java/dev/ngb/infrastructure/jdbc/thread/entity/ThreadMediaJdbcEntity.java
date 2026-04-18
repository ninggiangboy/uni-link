package dev.ngb.infrastructure.jdbc.thread.entity;

import dev.ngb.domain.thread.model.thread.MediaType;
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
@Table("thr_thread_medias")
public class ThreadMediaJdbcEntity extends JdbcEntity<Long> {

    private Long threadId;
    private MediaType type;
    private String url;
    private String thumbnailUrl;
    private Integer width;
    private Integer height;
    private Integer duration;
    private String altText;
    private Integer position;
}
