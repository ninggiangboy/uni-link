package dev.ngb.infrastructure.jdbc.base.repository;

import dev.ngb.domain.DomainEntity;
import dev.ngb.domain.Repository;
import dev.ngb.infrastructure.jdbc.base.entity.JdbcEntity;
import dev.ngb.infrastructure.jdbc.base.entity.SoftDeletable;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.util.StringUtils;
import dev.ngb.util.TimeProvider;
import org.jspecify.annotations.Nullable;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Transactional
public abstract class JdbcRepository<D extends DomainEntity<ID>, J extends JdbcEntity<ID>, ID>
        implements Repository<D, ID> {

    // Constants
    protected final String ID_COLUMN = "id";
    protected final String UUID_COLUMN = "uuid";
    protected final String DELETED_AT_COLUMN = "deleted_at";

    // Fields
    private final JdbcClient jdbcClient;
    protected final JdbcTemplate jdbcTemplate;
    private final JdbcAggregateTemplate jdbcAggregate;
    private final Class<J> clazz;
    private final boolean softDeleteSupported;
    private final JdbcMapper<D, J> mapper;

    // Constructor
    protected JdbcRepository(
            Class<J> clazz,
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate,
            JdbcMapper<D, J> mapper
    ) {
        this.clazz = clazz;
        this.jdbcClient = jdbcClient;
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcAggregate = jdbcAggregate;
        this.softDeleteSupported = SoftDeletable.class.isAssignableFrom(clazz);
        this.mapper = Objects.requireNonNull(mapper, "mapper must not be null");
    }

    // Mapping
    protected D mapToDomain(J entity) {
        return mapper.toDomain(entity);
    }

    protected J mapToJdbc(D entity) {
        return mapper.toJdbc(entity);
    }

    // Query Builder
    protected Query buildQuery(@Nullable Criteria criteria, @Nullable Pageable pageable) {
        Query query;

        if (!softDeleteSupported) {
            query = (criteria == null) ? Query.empty() : Query.query(criteria);
        } else {
            Criteria base = Criteria.where(DELETED_AT_COLUMN).isNull();
            Criteria finalCriteria = (criteria == null) ? base : base.and(criteria);
            query = Query.query(finalCriteria);
        }

        if (pageable != null) {
            if (pageable.getSort().isSorted()) {
                query = query.sort(pageable.getSort());
            }
            if (pageable.isPaged()) {
                query = query.offset(pageable.getOffset()).limit(pageable.getPageSize());
            }
        }

        return query;
    }

    // Criteria-based Queries
    protected List<D> findAll(@Nullable Criteria criteria, @Nullable Pageable pageable) {
        return jdbcAggregate
                .findAll(buildQuery(criteria, pageable), clazz)
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    protected List<D> findAll(@Nullable Criteria criteria) {
        return findAll(criteria, Pageable.unpaged());
    }

    protected Optional<D> findFirst(@Nullable Criteria criteria, @Nullable Sort sort) {
        Pageable pageable = sort != null && sort.isSorted()
                ? Pageable.unpaged(sort)
                : Pageable.unpaged();
        return jdbcAggregate
                .findAll(buildQuery(criteria, pageable).limit(1), clazz)
                .stream()
                .findFirst()
                .map(this::mapToDomain);
    }

    protected Optional<D> findFirst(@Nullable Criteria criteria) {
        return findFirst(criteria, Sort.unsorted());
    }

    protected long count(@Nullable Criteria criteria) {
        return jdbcAggregate.count(buildQuery(criteria, Pageable.unpaged()), clazz);
    }

    protected boolean exists(@Nullable Criteria criteria) {
        return jdbcAggregate
                .findAll(buildQuery(criteria, Pageable.unpaged()).limit(1), clazz)
                .iterator()
                .hasNext();
    }

    // Field-based helpers
    protected List<D> findAllByFieldEqual(String fieldName, Object value) {
        return findAll(Criteria.where(fieldName).is(value));
    }

    protected Optional<D> findFirstByFieldEqual(String fieldName, Object value) {
        return findFirst(Criteria.where(fieldName).is(value));
    }

    protected long countByFieldEqual(String fieldName, Object value) {
        return count(Criteria.where(fieldName).is(value));
    }

    protected boolean existsByFieldEqual(String fieldName, Object value) {
        return exists(Criteria.where(fieldName).is(value));
    }

    // SQL-based Queries
    @SuppressWarnings("SqlSourceToSinkFlow")
    protected List<D> findAllBySql(String sql, Map<String, ?> params) {
        return jdbcClient.sql(StringUtils.toSingleLine(sql))
                .params(params)
                .query(clazz)
                .list()
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    protected Optional<D> findOneBySql(String sql, Map<String, ?> params) {
        return jdbcClient.sql(StringUtils.toSingleLine(sql))
                .params(params)
                .query(clazz)
                .list()
                .stream()
                .findFirst()
                .map(this::mapToDomain);
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    protected long countBySql(String sql, Map<String, ?> params) {
        return jdbcClient.sql(StringUtils.toSingleLine(sql))
                .params(params)
                .query(Long.class)
                .single();
    }

    protected boolean existsBySql(String sql, Map<String, ?> params) {
        return countBySql(sql, params) > 0;
    }

    // Repository - Read
    @Override
    public List<D> findAll() {
        return findAll(null);
    }

    @Override
    public Optional<D> findById(ID id) {
        return findFirst(Criteria.where(ID_COLUMN).is(id));
    }

    @Override
    public Optional<D> findByUuid(String uuid) {
        return findFirst(Criteria.where(UUID_COLUMN).is(uuid));
    }

    @Override
    public List<D> findByIds(List<ID> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return findAll(Criteria.where(ID_COLUMN).in(ids));
    }

    @Override
    public List<D> findByUuids(List<String> uuids) {
        if (uuids == null || uuids.isEmpty()) return List.of();
        return findAll(Criteria.where(UUID_COLUMN).in(uuids));
    }

    @Override
    public boolean existsById(ID id) {
        return exists(Criteria.where(ID_COLUMN).is(id));
    }

    // Write Operations
    @Override
    public D save(D entity) {
        try {
            return mapToDomain(jdbcAggregate.save(mapToJdbc(entity)));
        } catch (OptimisticLockingFailureException ex) {
            throw new ConcurrentModificationException(
                    "Entity " + entity.getId() + " was modified concurrently"
            );
        }
    }

    @Override
    public List<D> saveAll(List<D> entities) {
        if (entities == null || entities.isEmpty()) return List.of();

        try {
            return jdbcAggregate.saveAll(
                            entities.stream().map(this::mapToJdbc).toList()
                    ).stream()
                    .map(this::mapToDomain)
                    .toList();
        } catch (OptimisticLockingFailureException ex) {
            throw new ConcurrentModificationException("Entities were modified concurrently");
        }
    }

    @Override
    public void delete(D entity) {
        if (!softDeleteSupported) {
            jdbcAggregate.deleteById(entity.getId(), clazz);
            return;
        }

        try {
            J jdbcEntity = mapToJdbc(entity);
            ((SoftDeletable) jdbcEntity).setDeletedAt(TimeProvider.now());
            jdbcAggregate.save(jdbcEntity);
        } catch (OptimisticLockingFailureException ex) {
            throw new ConcurrentModificationException(
                    "Entity " + entity.getId() + " was modified concurrently"
            );
        }
    }

    @Override
    public void deleteAll(List<D> entities) {
        if (entities == null || entities.isEmpty()) return;

        if (!softDeleteSupported) {
            jdbcAggregate.deleteAllById(
                    entities.stream().map(DomainEntity::getId).toList(),
                    clazz
            );
            return;
        }

        try {
            Instant now = TimeProvider.now();

            List<J> jdbcEntities = entities.stream()
                    .map(this::mapToJdbc)
                    .peek(e -> ((SoftDeletable) e).setDeletedAt(now))
                    .toList();

            jdbcAggregate.saveAll(jdbcEntities);

        } catch (OptimisticLockingFailureException ex) {
            throw new ConcurrentModificationException("Entities were modified concurrently");
        }
    }
}
