package dev.ngb.domain;

import java.util.List;
import java.util.Optional;

/**
 * Base repository interface for managing {@link DomainEntity domain entities}.
 * <p>
 * Repositories abstract persistence concerns from the domain model and provide
 * a collection-like interface for accessing and manipulating entities.
 *
 * @param <T>  the type of domain entity managed by the repository
 * @param <ID> the type of the entity identifier
 */
public interface Repository<T extends DomainEntity<ID>, ID> {

    /**
     * Retrieves all entities.
     *
     * @return a list of all entities
     */
    List<T> findAll();

    /**
     * Retrieves an entity by its identifier.
     *
     * @param id the entity identifier
     * @return an {@link Optional} containing the entity if found, otherwise empty
     */
    Optional<T> findById(ID id);

    Optional<T> findByUuid(String uuid);

    /**
     * Retrieves multiple entities by their identifiers.
     *
     * @param ids the list of entity identifiers
     * @return a list of entities matching the given identifiers
     */
    List<T> findByIds(List<ID> ids);

    List<T> findByUuids(List<String> uuids);

    /**
     * Checks whether an entity with the given identifier exists.
     *
     * @param id the entity identifier
     * @return {@code true} if the entity exists, otherwise {@code false}
     */
    boolean existsById(ID id);

    /**
     * Persists a new entity or updates an existing entity.
     *
     * @param entity the entity to create
     * @return the created entity
     */
    T save(T entity);

    /**
     * Persists multiple new entities or updates multiple existing entities.
     *
     * @param entities the entities to create
     * @return the list of created entities
     */
    List<T> saveAll(List<T> entities);

    /**
     * Deletes the given entity.
     *
     * @param entity the entity to delete
     */
    void delete(T entity);

    /**
     * Deletes multiple entities.
     *
     * @param entities the entities to delete
     */
    void deleteAll(List<T> entities);
}
