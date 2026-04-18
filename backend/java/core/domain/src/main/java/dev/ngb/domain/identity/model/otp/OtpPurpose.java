package dev.ngb.domain.identity.model.otp;

/**
 * Purposes for which a one-time password can be issued.
 */
public enum OtpPurpose {
    REGISTRATION,
    LOGIN,
    PASSWORD_RESET,
    EMAIL_VERIFICATION,
    PHONE_VERIFICATION,
    TWO_FACTOR
}

