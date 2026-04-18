package dev.ngb.domain.identity.model.auth;

/**
 * Lifecycle states for an account.
 */
public enum AccountStatus {
    PENDING,
    ACTIVE,
    SUSPENDED,
    BANNED,
    DEACTIVATED
}
