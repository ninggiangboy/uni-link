package dev.ngb.application.port.storage;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public interface ObjectStorage {

    boolean objectExists(String bucket, String objectKey);

    PresignedPutResult presignPut(String bucket, String objectKey, String contentType, Duration signatureDuration);

    byte[] getObjectBytes(String bucket, String objectKey);

    PutObjectResult putObject(String bucket, String objectKey, byte[] bytes, String contentType);

    record PresignedPutResult(
            String uploadUrl,
            Map<String, String> uploadHeaders,
            Instant expiresAt,
            String fileUrl,
            String bucket
    ) {
    }

    record PutObjectResult(
            String bucket,
            String objectKey,
            String fileUrl
    ) {
    }
}
