package dev.ngb.infrastructure.jdbc.base.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class SoftDeletableJdbcEntity<ID> extends JdbcEntity<ID> implements SoftDeletable {

    protected Instant deletedAt;
}
