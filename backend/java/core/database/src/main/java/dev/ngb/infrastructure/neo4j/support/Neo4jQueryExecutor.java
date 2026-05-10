package dev.ngb.infrastructure.neo4j.support;

import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

import org.neo4j.driver.Record;
import org.neo4j.driver.types.TypeSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class Neo4jQueryExecutor {

    private final Neo4jClient neo4jClient;

    public boolean queryBoolean(String query, Map<String, Object> params) {
        return neo4jClient.query(query)
                .bindAll(params)
                .fetchAs(Boolean.class)
                .mappedBy((_, record) -> record.get(0).asBoolean())
                .one()
                .orElse(false);
    }

    public <T> Optional<T> queryOne(String query, Map<String, Object> params,
                                    BiFunction<TypeSystem, Record, T> mapper, Class<T> type) {
        return neo4jClient.query(query)
                .bindAll(params)
                .fetchAs(type)
                .mappedBy(mapper::apply)
                .one();
    }

    public List<Long> queryLongList(String query, Map<String, Object> params, String key) {
        return new ArrayList<>(neo4jClient.query(query)
                .bindAll(params)
                .fetchAs(Long.class)
                .mappedBy((_, record) -> Objects.requireNonNull(Neo4jValueReader.longValue(record, key)))
                .all());
    }
}
