package dev.ngb.infrastructure.jdbc.event.entity;

import dev.ngb.infrastructure.jdbc.base.entity.JdbcEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table("msg_event_publications")
public class EventPublicationEntity extends JdbcEntity<Long> {

    private String type;
    private String typeClazz;
    private String payload;
    private Instant occurredAt;
}
