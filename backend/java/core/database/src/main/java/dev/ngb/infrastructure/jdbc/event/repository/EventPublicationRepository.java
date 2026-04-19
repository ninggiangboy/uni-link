package dev.ngb.infrastructure.jdbc.event.repository;

import dev.ngb.infrastructure.jdbc.event.entity.EventPublicationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface EventPublicationRepository extends CrudRepository<EventPublicationEntity, Long> {
}
