package dev.ngb.infrastructure.jdbc.base.entity;

import java.time.Instant;

public interface SoftDeletable {

    void setDeletedAt(Instant deletedAt);

}
