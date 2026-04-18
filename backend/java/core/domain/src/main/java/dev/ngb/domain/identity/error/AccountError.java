package dev.ngb.domain.identity.error;

import dev.ngb.domain.DomainError;
import dev.ngb.domain.DomainErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountError implements DomainError {

    ACCOUNT_NOT_FOUND("Account not found", DomainErrorType.NOT_FOUND),
    EMAIL_ALREADY_EXISTS("Email is already registered", DomainErrorType.CONFLICT),
    INVALID_CREDENTIALS("Invalid email or password", DomainErrorType.UNAUTHORIZED),
    ACCOUNT_NOT_ACTIVE("Account is not active", DomainErrorType.FORBIDDEN),
    ACCOUNT_SUSPENDED("Account has been suspended", DomainErrorType.FORBIDDEN),
    ACCOUNT_BANNED("Account has been banned", DomainErrorType.FORBIDDEN),
    ACCOUNT_PENDING("Account email has not been verified", DomainErrorType.FORBIDDEN),
    INVALID_OTP("Invalid or expired verification code", DomainErrorType.VALIDATION),
    OTP_MAX_ATTEMPTS("Maximum OTP verification attempts exceeded", DomainErrorType.RATE_LIMITED),
    EMAIL_ALREADY_VERIFIED("Email has already been verified", DomainErrorType.CONFLICT),
    INVALID_VERIFICATION_TOKEN("Invalid or expired verification token", DomainErrorType.VALIDATION),
    INVALID_OAUTH_TOKEN("Invalid OAuth provider token", DomainErrorType.UNAUTHORIZED),
    OAUTH_EMAIL_CONFLICT("Email is already linked to another account", DomainErrorType.CONFLICT),
    INVALID_REFRESH_TOKEN("Invalid or expired refresh token", DomainErrorType.UNAUTHORIZED),
    SESSION_REVOKED("Session has been revoked", DomainErrorType.UNAUTHORIZED),
    PASSWORD_SAME_AS_OLD("New password must be different from current password", DomainErrorType.VALIDATION);

    private final String message;
    private final DomainErrorType type;
}
