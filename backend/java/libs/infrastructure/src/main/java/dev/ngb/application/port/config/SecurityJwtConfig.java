package dev.ngb.application.port.config;

public interface SecurityJwtConfig {
    long securityJwtAccessTokenExpiry();

    long securityJwtRefreshTokenExpiry();

    String securityJwtPublicKeyBase64();

    String securityJwtPrivateKeyBase64();
}
