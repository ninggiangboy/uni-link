package dev.ngb.infrastructure.jdbc.thread.entity;

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
@Table("thr_thread_link_previews")
public class ThreadLinkPreviewJdbcEntity extends JdbcEntity<Long> {

    private Long threadId;
    private String url;
    private String title;
    private String description;
    private String imageUrl;
}
