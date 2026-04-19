package dev.ngb.application.port.config;

public interface StorageS3Config {
    String storageAccessKeyId();

    String storageSecretAccessKey();

    String storageS3Region();

    String storageS3Bucket();

    /**
     * Optional CDN or public origin base URL (no trailing slash). When blank, URLs are derived from region/bucket.
     */
    String storageS3PublicBaseUrl();

    /**
     * Optional custom endpoint (e.g. LocalStack, MinIO). When blank, the default AWS endpoint for the region is used.
     */
    String storageS3Endpoint();
}
