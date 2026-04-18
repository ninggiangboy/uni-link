package dev.ngb.domain;

public enum DomainErrorType {
    INVALID,        // 400
    UNAUTHORIZED,   // 401
    FORBIDDEN,      // 403
    NOT_FOUND,      // 404
    CONFLICT,       // 409
    VALIDATION,     // 422
    RATE_LIMITED,   // 429
}
