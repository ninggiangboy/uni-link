package dev.ngb.app.config.graphql;

import dev.ngb.domain.DomainErrorType;
import dev.ngb.domain.DomainException;
import dev.ngb.util.validation.ValidationError;
import dev.ngb.util.validation.ValidationException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Maps domain and validation failures to GraphQL errors with extensions aligned with {@link dev.ngb.web.ErrorResponse}.
 */
@Component
public class DomainExceptionDataFetcherResolver implements DataFetcherExceptionResolver {

    private static final Map<DomainErrorType, HttpStatus> STATUS_BY_TYPE = new EnumMap<>(DomainErrorType.class);

    static {
        STATUS_BY_TYPE.put(DomainErrorType.INVALID, HttpStatus.BAD_REQUEST);
        STATUS_BY_TYPE.put(DomainErrorType.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        STATUS_BY_TYPE.put(DomainErrorType.FORBIDDEN, HttpStatus.FORBIDDEN);
        STATUS_BY_TYPE.put(DomainErrorType.NOT_FOUND, HttpStatus.NOT_FOUND);
        STATUS_BY_TYPE.put(DomainErrorType.CONFLICT, HttpStatus.CONFLICT);
        STATUS_BY_TYPE.put(DomainErrorType.VALIDATION, HttpStatus.UNPROCESSABLE_CONTENT);
        STATUS_BY_TYPE.put(DomainErrorType.RATE_LIMITED, HttpStatus.TOO_MANY_REQUESTS);
    }

    @Override
    public Mono<List<GraphQLError>> resolveException(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof DomainException de) {
            HttpStatus status = STATUS_BY_TYPE.getOrDefault(de.getError().getType(), HttpStatus.BAD_REQUEST);
            Map<String, Object> extensions = extensionsForDomain(de, status);
            GraphQLError error = GraphqlErrorBuilder.newError(env)
                    .message(de.getError().getMessage())
                    .extensions(extensions)
                    .build();
            return Mono.just(List.of(error));
        }
        if (ex instanceof ValidationException ve) {
            Map<String, List<String>> details = ve.errors().stream()
                    .collect(Collectors.groupingBy(
                            ValidationError::field,
                            Collectors.mapping(ValidationError::message, Collectors.toList())
                    ));
            Map<String, Object> extensions = new HashMap<>();
            extensions.put("error", "VALIDATION_ERROR");
            extensions.put("message", "Request validation failed");
            extensions.put("timestamp", Instant.now().toString());
            extensions.put("httpStatus", HttpStatus.BAD_REQUEST.value());
            extensions.put("details", details);
            GraphQLError error = GraphqlErrorBuilder.newError(env)
                    .message("Request validation failed")
                    .extensions(extensions)
                    .build();
            return Mono.just(List.of(error));
        }
        return Mono.empty();
    }

    private static Map<String, Object> extensionsForDomain(DomainException de, HttpStatus status) {
        Map<String, Object> extensions = new HashMap<>();
        extensions.put("error", de.getError().name());
        extensions.put("message", de.getError().getMessage());
        extensions.put("timestamp", Instant.now().toString());
        extensions.put("httpStatus", status.value());
        if (de.getDetails() != null && !de.getDetails().isEmpty()) {
            extensions.put("details", de.getDetails());
        }
        return extensions;
    }
}
