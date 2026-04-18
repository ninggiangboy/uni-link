package dev.ngb.infrastructure.jdbc.thread.entity;

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
@Table("thr_thread_polls")
public class ThreadPollJdbcEntity extends JdbcEntity<Long> {

    private Long threadId;
    private Instant expiresAt;
    private Boolean allowMultipleVotes;
}
