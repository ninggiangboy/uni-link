package dev.ngb.infrastructure.neo4j.support;

import org.neo4j.driver.types.MapAccessor;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

public final class Neo4jValueReader {

    private Neo4jValueReader() {}

    public static Node node(Map<String, Object> row, String key) {
        return (Node) row.get(key);
    }

    public static Relationship relationship(Map<String, Object> row, String key) {
        return (Relationship) row.get(key);
    }

    public static Long longValue(MapAccessor source, String key) {
        if (source.get(key).isNull()) {
            return null;
        }
        Object value = source.get(key).asObject();
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(value.toString());
    }

    public static Integer intValue(MapAccessor source, String key) {
        if (source.get(key).isNull()) {
            return null;
        }
        Object value = source.get(key).asObject();
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(value.toString());
    }

    public static String stringValue(MapAccessor source, String key) {
        if (source.get(key).isNull()) {
            return null;
        }
        Object value = source.get(key).asObject();
        return value == null ? null : value.toString();
    }

    public static Boolean booleanValue(MapAccessor source, String key) {
        if (source.get(key).isNull()) {
            return null;
        }
        Object value = source.get(key).asObject();
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        return Boolean.parseBoolean(value.toString());
    }

    public static Instant instantValue(MapAccessor source, String key) {
        if (source.get(key).isNull()) {
            return null;
        }
        Object value = source.get(key).asObject();
        if (value == null) {
            return null;
        }
        if (value instanceof Instant instant) {
            return instant;
        }
        if (value instanceof OffsetDateTime offsetDateTime) {
            return offsetDateTime.toInstant();
        }
        if (value instanceof ZonedDateTime zonedDateTime) {
            return zonedDateTime.toInstant();
        }
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.toInstant(ZoneOffset.UTC);
        }
        return Instant.parse(value.toString());
    }

    public static <E extends Enum<E>> E enumValue(MapAccessor source, String key, Class<E> enumType) {
        String value = stringValue(source, key);
        if (value == null) {
            return null;
        }
        return Enum.valueOf(enumType, value);
    }
}
