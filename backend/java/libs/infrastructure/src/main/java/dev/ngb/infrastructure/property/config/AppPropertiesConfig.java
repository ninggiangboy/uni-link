package dev.ngb.infrastructure.property.config;

import dev.ngb.application.port.config.AppConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public record AppPropertiesConfig(

        // Security JWT
        @Value("${app.security.jwt.access-token-expiry:900}")
        long securityJwtAccessTokenExpiry,

        @Value("${app.security.jwt.refresh-token-expiry:300}")
        long securityJwtRefreshTokenExpiry,

        @Value("${app.security.jwt.verification-token-expiry:600}")
        long securityJwtVerificationTokenExpiry,

        @Value("${app.security.jwt.public-key-base64}")
        String securityJwtPublicKeyBase64,

        @Value("${app.security.jwt.private-key-base64}")
        String securityJwtPrivateKeyBase64,

        // Storage S3
        @Value("${app.storage.s3.access-key-id:}")
        String storageAccessKeyId,

        @Value("${app.storage.s3.secret-access-key:}")
        String storageSecretAccessKey,

        @Value("${app.storage.s3.region:us-east-1}")
        String storageS3Region,

        @Value("${app.storage.s3.bucket:}")
        String storageS3Bucket,

        @Value("${app.storage.s3.public-base-url:}")
        String storageS3PublicBaseUrl,

        @Value("${app.storage.s3.endpoint:}")
        String storageS3Endpoint

) implements AppConfig {}
