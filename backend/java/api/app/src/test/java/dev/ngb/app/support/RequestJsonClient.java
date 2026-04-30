package dev.ngb.app.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ngb.web.ErrorResponse;
import io.vavr.control.Either;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * JSON POST client: {@link #postJson} for raw responses, {@link #post} for {@link Either} parsing.
 * {@code Left} defaults to {@link ErrorResponse}.
 */
public final class RequestJsonClient {

    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final RestTemplate restTemplate;

    public RequestJsonClient(ObjectMapper objectMapper, String baseUrl, RestTemplate restTemplate) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper");
        this.baseUrl = Objects.requireNonNull(baseUrl, "baseUrl");
        this.restTemplate = Objects.requireNonNull(restTemplate, "restTemplate");
    }

    public ResponseEntity<String> postJson(String path, Object body, HttpHeaders headers) {
        var h = headers != null ? headers : defaultJsonHeaders();
        return exchangeJson(path, HttpMethod.POST, body, h);
    }

    public ResponseEntity<String> patchJson(String path, Object body, HttpHeaders headers) {
        var h = headers != null ? headers : defaultJsonHeaders();
        return exchangeJson(path, HttpMethod.PATCH, body, h);
    }

    public ResponseEntity<String> deleteJson(String path, Object body, HttpHeaders headers) {
        var h = headers != null ? headers : defaultJsonHeaders();
        return exchangeJson(path, HttpMethod.DELETE, body, h);
    }

    public <R> Either<ErrorResponse, R> post(
            String path,
            Object body,
            Class<R> type
    ) {
        return post(path, body, null, type, HttpStatusCode::is2xxSuccessful);
    }

    public <R> Either<ErrorResponse, R> post(
            String path,
            Object body,
            HttpHeaders headers,
            Class<R> type
    ) {
        return post(path, body, headers, type, HttpStatusCode::is2xxSuccessful);
    }

    public <R> Either<ErrorResponse, R> post(
            String path,
            Object body,
            HttpHeaders headers,
            Class<R> type,
            Predicate<HttpStatusCode> isSuccess
    ) {
        var raw = postJson(path, body, headers);
        return deserialize(path, raw, type, isSuccess);
    }

    public <R> Either<ErrorResponse, R> patch(
            String path,
            Object body,
            Class<R> type
    ) {
        var raw = patchJson(path, body, null);
        return deserialize(path, raw, type, HttpStatusCode::is2xxSuccessful);
    }

    public <R> Either<ErrorResponse, R> delete(
            String path,
            Object body,
            Class<R> type
    ) {
        var raw = deleteJson(path, body, null);
        return deserialize(path, raw, type, HttpStatusCode::is2xxSuccessful);
    }

    private <R> Either<ErrorResponse, R> deserialize(
            String path,
            ResponseEntity<String> raw,
            Class<R> type,
            Predicate<HttpStatusCode> isSuccess
    ) {
        var status = raw.getStatusCode();
        var responseBody = raw.getBody();
        if (isSuccess.test(status)) {
            if (type == NoContent.class) {
                @SuppressWarnings("unchecked")
                R empty = (R) NoContent.INSTANCE;
                return Either.right(empty);
            }
            requireNonBlankBody(path, status, responseBody);
            try {
                return Either.right(objectMapper.readValue(responseBody, type));
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Failed to deserialize success body for " + path, e);
            }
        }
        if (responseBody == null || responseBody.isBlank()) {
            var fallback = ErrorResponse.of("HTTP_" + status.value(), "Request failed with status " + status.value());
            return Either.left(fallback);
        }
        try {
            return Either.left(objectMapper.readValue(responseBody, ErrorResponse.class));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize error body for " + path, e);
        }
    }

    private ResponseEntity<String> exchangeJson(String path, HttpMethod method, Object body, HttpHeaders headers) {
        return restTemplate.exchange(
                baseUrl + path,
                method,
                new HttpEntity<>(body, headers),
                String.class
        );
    }

    private static HttpHeaders defaultJsonHeaders() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static void requireNonBlankBody(String path, HttpStatusCode status, String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            throw new IllegalStateException("Empty response body for " + path + ", status=" + status);
        }
    }
}
