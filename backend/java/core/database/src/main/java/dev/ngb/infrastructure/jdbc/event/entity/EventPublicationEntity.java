package dev.ngb.infrastructure.jdbc.event.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("msg_event_publications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventPublicationEntity {

    @Id
    private UUID id;

    private String type;

    private String typeClazz;

    private String payload;

    private Instant occurredAt;

    private Instant createdAt;
}
