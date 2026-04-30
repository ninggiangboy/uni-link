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
        HttpHeaders h = headers != null ? headers : defaultJsonHeaders();
        return restTemplate.exchange(
                baseUrl + path,
                HttpMethod.POST,
                new HttpEntity<>(body, h),
                String.class
        );
    }

    public <R> Either<ErrorResponse, R> post(
            String path,
            Object body,
            Class<R> rightType
    ) {
        return post(path, body, null, ErrorResponse.class, rightType, HttpStatusCode::is2xxSuccessful);
    }

    public <R> Either<ErrorResponse, R> post(
            String path,
            Object body,
            HttpHeaders headers,
            Class<R> rightType
    ) {
        return post(path, body, headers, ErrorResponse.class, rightType, HttpStatusCode::is2xxSuccessful);
    }

    public <L, R> Either<L, R> post(
            String path,
            Object body,
            HttpHeaders headers,
            Class<L> leftType,
            Class<R> rightType,
            Predicate<HttpStatusCode> isSuccess
    ) {
        ResponseEntity<String> raw = postJson(path, body, headers);
        HttpStatusCode status = raw.getStatusCode();
        String responseBody = raw.getBody();
        if (isSuccess.test(status)) {
            if (rightType == NoContent.class) {
                @SuppressWarnings("unchecked")
                R empty = (R) NoContent.INSTANCE;
                return Either.right(empty);
            }
            requireNonBlankBody(path, status, responseBody);
            try {
                return Either.right(objectMapper.readValue(responseBody, rightType));
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Failed to deserialize success body for " + path, e);
            }
        }
        requireNonBlankBody(path, status, responseBody);
        try {
            return Either.left(objectMapper.readValue(responseBody, leftType));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize error body for " + path, e);
        }
    }

    private static HttpHeaders defaultJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
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
