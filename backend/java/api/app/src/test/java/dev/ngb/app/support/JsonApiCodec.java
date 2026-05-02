package dev.ngb.app.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ngb.web.ErrorResponse;
import io.vavr.control.Either;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * JSON request encoding and response decoding for API integration tests (symmetric
 * {@link ObjectMapper} usage for bodies).
 */
public final class JsonApiCodec {

    private final ObjectMapper objectMapper;

    public JsonApiCodec(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper");
    }

    public Either<ErrorResponse, String> serializeRequest(Object body) {
        try {
            return Either.right(objectMapper.writeValueAsString(body));
        } catch (JsonProcessingException e) {
            return Either.left(ErrorResponse.of(
                    "SERIALIZATION_ERROR",
                    "Failed to serialize request body: " + e.getOriginalMessage()
            ));
        }
    }

    public <R> Either<ErrorResponse, R> deserialize(
            String path,
            ResponseEntity<String> raw,
            Class<R> type,
            Predicate<HttpStatusCode> isSuccess
    ) {
        var status = raw.getStatusCode();
        var responseBody = raw.getBody();
        if (isSuccess.test(status)) {
            if (type == EmptyBody.class) {
                @SuppressWarnings("unchecked")
                R empty = (R) EmptyBody.INSTANCE;
                return Either.right(empty);
            }
            if (responseBody == null || responseBody.isBlank()) {
                return Either.left(ErrorResponse.of(
                        "EMPTY_SUCCESS_BODY",
                        "Empty response body for " + path + ", status=" + status.value()
                ));
            }
            try {
                return Either.right(objectMapper.readValue(responseBody, type));
            } catch (JsonProcessingException e) {
                return Either.left(ErrorResponse.of(
                        "DESERIALIZATION_ERROR",
                        "Failed to deserialize success body for " + path + ": " + e.getOriginalMessage()
                ));
            }
        }
        if (responseBody == null || responseBody.isBlank()) {
            var fallback = ErrorResponse.of("HTTP_" + status.value(), "Request failed with status " + status.value());
            return Either.left(fallback);
        }
        try {
            return Either.left(objectMapper.readValue(responseBody, ErrorResponse.class));
        } catch (JsonProcessingException e) {
            return Either.left(ErrorResponse.of(
                    "ERROR_BODY_DESERIALIZATION",
                    "Failed to deserialize error body for " + path + ": " + e.getOriginalMessage()
            ));
        }
    }
}
