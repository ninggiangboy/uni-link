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
@Table("thr_thread_poll_options")
public class ThreadPollOptionJdbcEntity extends JdbcEntity<Long> {

    private Long threadId;
    private String option;
    private Integer position;
    private Long voteCount;
}
