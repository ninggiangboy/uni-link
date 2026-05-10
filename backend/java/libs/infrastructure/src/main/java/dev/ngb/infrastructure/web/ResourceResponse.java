package dev.ngb.infrastructure.web;

import dev.ngb.domain.DomainErrorType;
import dev.ngb.domain.DomainException;
import dev.ngb.web.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

@UtilityClass
public final class ResourceResponse {

    private static final Map<DomainErrorType, HttpStatus> ERROR_MAP;

    static {
        var map = new EnumMap<DomainErrorType, HttpStatus>(DomainErrorType.class);
        map.put(DomainErrorType.INVALID, HttpStatus.BAD_REQUEST);
        map.put(DomainErrorType.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        map.put(DomainErrorType.FORBIDDEN, HttpStatus.FORBIDDEN);
        map.put(DomainErrorType.NOT_FOUND, HttpStatus.NOT_FOUND);
        map.put(DomainErrorType.CONFLICT, HttpStatus.CONFLICT);
        map.put(DomainErrorType.VALIDATION, HttpStatus.UNPROCESSABLE_CONTENT);
        map.put(DomainErrorType.RATE_LIMITED, HttpStatus.TOO_MANY_REQUESTS);
        ERROR_MAP = Collections.unmodifiableMap(map);
    }

    public static <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity.ok(body);
    }

    public static <T> ResponseEntity<T> created(T body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    public static ResponseEntity<Void> accepted() {
        return ResponseEntity.accepted().build();
    }

    public static <T> ResponseEntity<T> okWithCookie(T body, ResponseCookie cookie) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(body);
    }

    public static ResponseEntity<Void> noContentWithCookie(ResponseCookie cookie) {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    public static ResponseCookie buildHttpOnlyCookie(
            String cookieName,
            String value,
            String path,
            long maxAgeSeconds,
            HttpServletRequest request
    ) {
        return ResponseCookie.from(cookieName, value)
                .httpOnly(true)
                .secure(isSecureRequest(request))
                .path(path)
                .sameSite("Lax")
                .maxAge(maxAgeSeconds)
                .build();
    }

    public static ResponseCookie buildClearHttpOnlyCookie(
            String cookieName,
            String path,
            HttpServletRequest request
    ) {
        return buildHttpOnlyCookie(cookieName, "", path, 0, request);
    }

    public static ResponseEntity<ErrorResponse> domainError(DomainException ex) {
        HttpStatus status = ERROR_MAP.getOrDefault(ex.getError().getType(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(status).body(ErrorResponse.of(ex));
    }

    public static ResponseEntity<ErrorResponse> badRequest(ErrorResponse error) {
        return ResponseEntity.badRequest().body(error);
    }

    public static ResponseEntity<ErrorResponse> serverError(ErrorResponse error) {
        return ResponseEntity.internalServerError().body(error);
    }

    private static boolean isSecureRequest(HttpServletRequest request) {
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        if (forwardedProto != null) {
            return "https".equalsIgnoreCase(forwardedProto);
        }
        return request.isSecure();
    }
}
