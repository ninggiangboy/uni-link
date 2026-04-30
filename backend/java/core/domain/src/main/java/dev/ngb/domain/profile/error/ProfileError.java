package dev.ngb.domain.profile.error;

import dev.ngb.domain.DomainError;
import dev.ngb.domain.DomainErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProfileError implements DomainError {

    ACCOUNT_NOT_ACTIVE("Account is not active", DomainErrorType.FORBIDDEN),
    USERNAME_ALREADY_EXISTS("Username is already taken", DomainErrorType.CONFLICT);

    private final String message;
    private final DomainErrorType type;
}

