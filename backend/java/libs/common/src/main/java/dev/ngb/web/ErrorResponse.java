package dev.ngb.web;

import dev.ngb.domain.DomainException;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        String error,
        String message,
        Instant timestamp,
        Map<String, Object> details
) {
    public static ErrorResponse of(DomainException ex) {
        return new ErrorResponse(
                ex.getError().name(),
                ex.getError().getMessage(),
                Instant.now(),
                ex.getDetails()
        );
    }

    public static ErrorResponse of(String error, String message) {
        return new ErrorResponse(error, message, Instant.now(), null);
    }
}
