package dev.ngb.app.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ngb.web.ErrorResponse;
import io.vavr.control.Either;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * JSON HTTP client using {@link RestTemplate}: transport, {@link SessionCookieStore}, and
 * {@link JsonApiCodec} are composed here. {@code Left} defaults to {@link ErrorResponse}.
 * Empty successful bodies use {@link EmptyBody}.
 */
public final class HttpJsonClient {

    private final String baseUrl;
    private final RestTemplate restTemplate;
    private final JsonApiCodec codec;
    private final SessionCookieStore cookieStore;

    public HttpJsonClient(String baseUrl, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this(baseUrl, restTemplate, new JsonApiCodec(objectMapper), new SessionCookieStore());
    }

    HttpJsonClient(String baseUrl, RestTemplate restTemplate, JsonApiCodec codec, SessionCookieStore cookieStore) {
        this.baseUrl = Objects.requireNonNull(baseUrl, "baseUrl");
        this.restTemplate = Objects.requireNonNull(restTemplate, "restTemplate");
        this.codec = Objects.requireNonNull(codec, "codec");
        this.cookieStore = Objects.requireNonNull(cookieStore, "cookieStore");
    }

    public Either<ErrorResponse, ResponseEntity<String>> postJson(String path, Object body) {
        return postJson(path, body, defaultJsonHeaders());
    }

    public Either<ErrorResponse, ResponseEntity<String>> postJson(String path, Object body, HttpHeaders headers) {
        Objects.requireNonNull(headers, "headers");
        return exchange(HttpMethod.POST, path, body, headers);
    }

    public Either<ErrorResponse, ResponseEntity<String>> patchJson(String path, Object body) {
        return patchJson(path, body, defaultJsonHeaders());
    }

    public Either<ErrorResponse, ResponseEntity<String>> patchJson(String path, Object body, HttpHeaders headers) {
        Objects.requireNonNull(headers, "headers");
        return exchange(HttpMethod.PATCH, path, body, headers);
    }

    public Either<ErrorResponse, ResponseEntity<String>> deleteJson(String path, Object body) {
        return deleteJson(path, body, defaultJsonHeaders());
    }

    public Either<ErrorResponse, ResponseEntity<String>> deleteJson(String path, Object body, HttpHeaders headers) {
        Objects.requireNonNull(headers, "headers");
        return exchange(HttpMethod.DELETE, path, body, headers);
    }

    public <R> Either<ErrorResponse, R> post(String path, Object body, Class<R> type) {
        return post(path, body, defaultJsonHeaders(), type);
    }

    public <R> Either<ErrorResponse, R> post(String path, Object body, HttpHeaders headers, Class<R> type) {
        Objects.requireNonNull(headers, "headers");
        return post(path, body, headers, type, HttpStatusCode::is2xxSuccessful);
    }

    public <R> Either<ErrorResponse, R> post(
            String path,
            Object body,
            HttpHeaders headers,
            Class<R> type,
            Predicate<HttpStatusCode> isSuccess
    ) {
        Objects.requireNonNull(headers, "headers");
        return postJson(path, body, headers)
                .flatMap(raw -> codec.deserialize(path, raw, type, isSuccess));
    }

    public <R> Either<ErrorResponse, R> patch(String path, Object body, Class<R> type) {
        return patch(path, body, defaultJsonHeaders(), type);
    }

    public <R> Either<ErrorResponse, R> patch(String path, Object body, HttpHeaders headers, Class<R> type) {
        Objects.requireNonNull(headers, "headers");
        return patchJson(path, body, headers)
                .flatMap(raw -> codec.deserialize(path, raw, type, HttpStatusCode::is2xxSuccessful));
    }

    public <R> Either<ErrorResponse, R> delete(String path, Object body, Class<R> type) {
        return delete(path, body, defaultJsonHeaders(), type);
    }

    public <R> Either<ErrorResponse, R> delete(String path, Object body, HttpHeaders headers, Class<R> type) {
        Objects.requireNonNull(headers, "headers");
        return deleteJson(path, body, headers)
                .flatMap(raw -> codec.deserialize(path, raw, type, HttpStatusCode::is2xxSuccessful));
    }

    public String getCookie(String cookieName) {
        return cookieStore.get(cookieName);
    }

    public <R> Either<ErrorResponse, R> get(String path, HttpHeaders headers, Class<R> type) {
        Objects.requireNonNull(headers, "headers");
        try {
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.putAll(headers);
            cookieStore.apply(requestHeaders);
            ResponseEntity<String> response = restTemplate.exchange(
                    resolveRequestUrl(path),
                    HttpMethod.GET,
                    new HttpEntity<>(requestHeaders),
                    String.class
            );
            cookieStore.captureFrom(response.getHeaders());
            return codec.deserialize(path, response, type, HttpStatusCode::is2xxSuccessful);
        } catch (RestClientException e) {
            return Either.left(ErrorResponse.of(
                    "TRANSPORT_ERROR",
                    "HTTP GET failed for " + path + ": " + e.getMessage()
            ));
        }
    }

    /**
     * DELETE without a JSON body (e.g. {@code DELETE /resource/id}).
     */
    public <R> Either<ErrorResponse, R> deleteWithoutBody(String path, HttpHeaders headers, Class<R> type) {
        Objects.requireNonNull(headers, "headers");
        try {
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.putAll(headers);
            cookieStore.apply(requestHeaders);
            ResponseEntity<String> response = restTemplate.exchange(
                    resolveRequestUrl(path),
                    HttpMethod.DELETE,
                    new HttpEntity<>(requestHeaders),
                    String.class
            );
            cookieStore.captureFrom(response.getHeaders());
            return codec.deserialize(path, response, type, HttpStatusCode::is2xxSuccessful);
        } catch (RestClientException e) {
            return Either.left(ErrorResponse.of(
                    "TRANSPORT_ERROR",
                    "HTTP DELETE failed for " + path + ": " + e.getMessage()
            ));
        }
    }

    private Either<ErrorResponse, ResponseEntity<String>> exchange(
            HttpMethod method,
            String path,
            Object body,
            HttpHeaders headers
    ) {
        Either<ErrorResponse, String> payload = codec.serializeRequest(body);
        if (payload.isLeft()) {
            return Either.left(payload.getLeft());
        }
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.putAll(headers);
        cookieStore.apply(requestHeaders);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    resolveRequestUrl(path),
                    method,
                    new HttpEntity<>(payload.get(), requestHeaders),
                    String.class
            );
            cookieStore.captureFrom(response.getHeaders());
            return Either.right(response);
        } catch (RestClientException e) {
            return Either.left(ErrorResponse.of(
                    "TRANSPORT_ERROR",
                    "HTTP exchange failed for " + path + ": " + e.getMessage()
            ));
        }
    }

    private String resolveRequestUrl(String path) {
        String normalizedBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        return normalizedBase + normalizedPath;
    }

    private static HttpHeaders defaultJsonHeaders() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
