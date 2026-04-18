package dev.ngb.infrastructure.jdbc.base.helper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.sql.IdentifierProcessing;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class JdbcMetadataHelper {

    private final JdbcMappingContext mappingContext;
    private final Dialect dialect;
    protected final JdbcConverter jdbcConverter;

    private final Map<String, String> columnCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void preloadAll() {
        for (RelationalPersistentEntity<?> entity : mappingContext.getPersistentEntities()) {

            if (!entity.isAnnotationPresent(Table.class)) {
                continue;
            }

            Class<?> entityClass = entity.getType();

            for (RelationalPersistentProperty property : entity) {

                String cacheKey = entityClass.getName() + "#" + property.getName();

                columnCache.computeIfAbsent(cacheKey, key ->
                        property.getColumnName()
                                .toSql(identifierProcessing())
                );
            }
        }
    }

    @FunctionalInterface
    public interface PropertyFunction<T, R> extends Serializable {
        R apply(T source);
    }

    private IdentifierProcessing identifierProcessing() {
        return dialect.getIdentifierProcessing();
    }

    public String resolveTableName(Class<?> entityClass) {
        RelationalPersistentEntity<?> entity =
                mappingContext.getRequiredPersistentEntity(entityClass);

        return entity.getTableName().toSql(identifierProcessing());
    }

    public List<RelationalPersistentProperty> getPersistentProperties(Class<?> entityClass, boolean includeId) {
        RelationalPersistentEntity<?> entity = mappingContext.getRequiredPersistentEntity(entityClass);
        return StreamSupport.stream(entity.spliterator(), false)
                .filter(p -> includeId || !p.isIdProperty())
                .filter(p -> !p.isTransient())
                .toList();
    }

    public List<String> resolveColumnNames(List<RelationalPersistentProperty> properties) {
        return properties.stream()
                .map(p -> p.getColumnName().toSql(identifierProcessing()))
                .toList();
    }

    public List<String> resolveColumnNames(Class<?> entityClass, boolean includeId) {
        List<RelationalPersistentProperty> properties = getPersistentProperties(entityClass, includeId);
        return resolveColumnNames(properties);
    }

    public String resolveColumnName(PropertyFunction<?, ?> fn) {
        SerializedLambda lambda = extract(fn);

        String cacheKey = lambda.getImplClass() + "#" + lambda.getImplMethodName();

        return columnCache.computeIfAbsent(cacheKey, key -> {
            String className = lambda.getImplClass().replace('/', '.');
            String propertyName = resolvePropertyName(lambda);

            try {
                Class<?> entityClass = Class.forName(className);

                RelationalPersistentEntity<?> entity =
                        mappingContext.getRequiredPersistentEntity(entityClass);

                RelationalPersistentProperty property =
                        entity.getRequiredPersistentProperty(propertyName);

                return property.getColumnName()
                        .toSql(dialect.getIdentifierProcessing());

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public RelationalPersistentEntity<?> getRequiredPersistentEntity(Class<?> entityClass) {
        return mappingContext.getRequiredPersistentEntity(entityClass);
    }

    public BiConsumer<PreparedStatement, Object> createInsertBinder(Class<?> entityClass) {

        List<RelationalPersistentProperty> properties =
                getPersistentProperties(entityClass, false);

        RelationalPersistentEntity<?> persistentEntity =
                getRequiredPersistentEntity(entityClass);

        return (ps, entity) -> {

            PersistentPropertyAccessor<?> accessor =
                    persistentEntity.getPropertyAccessor(entity);

            for (int i = 0; i < properties.size(); i++) {

                RelationalPersistentProperty property = properties.get(i);

                Object value = accessor.getProperty(property);

                Object jdbcValue = jdbcConverter.writeValue(
                        value,
                        property.getTypeInformation()
                );

                try {
                    ps.setObject(i + 1, jdbcValue);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }


    private SerializedLambda extract(PropertyFunction<?, ?> fn) {
        try {
            var method = fn.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            return (SerializedLambda) method.invoke(fn);
        } catch (Exception e) {
            throw new RuntimeException("Cannot extract lambda", e);
        }
    }

    private String resolvePropertyName(SerializedLambda lambda) {
        String methodName = lambda.getImplMethodName();

        if (methodName.startsWith("get")) {
            return decapitalize(methodName.substring(3));
        }

        if (methodName.startsWith("is")) {
            return decapitalize(methodName.substring(2));
        }

        return decapitalize(methodName);
    }

    private String decapitalize(String value) {
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }
}
