package dev.ngb.infrastructure.aws.s3.storage;

import dev.ngb.application.port.storage.ObjectStorage;
import dev.ngb.application.port.config.AppConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
@Slf4j
public class AwsS3ObjectStorage implements ObjectStorage {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final AppConfig appConfig;

    @Override
    public boolean objectExists(String bucket, String objectKey) {
        try {
            s3Client.headObject(HeadObjectRequest.builder().bucket(bucket).key(objectKey).build());
            return true;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }
            throw e;
        }
    }

    @Override
    public PresignedPutResult presignPut(String bucket, String objectKey, String contentType, Duration signatureDuration) {
        PutObjectRequest putObject = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(signatureDuration)
                .putObjectRequest(putObject)
                .build();
        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);
        SdkHttpRequest http = presigned.httpRequest();
        Map<String, String> headers = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> e : http.headers().entrySet()) {
            if (!e.getValue().isEmpty()) {
                headers.put(e.getKey(), String.join(", ", e.getValue()));
            }
        }
        Instant expiresAt = presigned.expiration();
        String fileUrl = buildPublicFileUrl(bucket, objectKey);
        return new PresignedPutResult(
                presigned.url().toExternalForm(),
                headers,
                expiresAt,
                fileUrl,
                bucket
        );
    }

    @Override
    public byte[] getObjectBytes(String bucket, String objectKey) {
        try {
            ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(
                    GetObjectRequest.builder().bucket(bucket).key(objectKey).build()
            );
            return response.asByteArray();
        } catch (NoSuchKeyException ex) {
            throw new IllegalStateException("Object not found in storage: " + objectKey, ex);
        }
    }

    @Override
    public PutObjectResult putObject(String bucket, String objectKey, byte[] bytes, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(contentType)
                .build();
        s3Client.putObject(request, RequestBody.fromBytes(bytes));
        return new PutObjectResult(bucket, objectKey, buildPublicFileUrl(bucket, objectKey));
    }

    private String buildPublicFileUrl(String bucket, String objectKey) {
        String publicBase = appConfig.storageS3PublicBaseUrl();
        if (publicBase != null && !publicBase.isBlank()) {
            String base = publicBase.replaceAll("/+$", "");
            return base + "/" + objectKey;
        }
        if (bucket == null || bucket.isBlank()) {
            return "";
        }
        String endpoint = appConfig.storageS3Endpoint();
        if (endpoint != null && !endpoint.isBlank()) {
            String e = endpoint.replaceAll("/+$", "");
            return e + "/" + bucket + "/" + objectKey;
        }
        return S3Utilities.builder()
                .region(Region.of(appConfig.storageS3Region()))
                .build()
                .getUrl(GetUrlRequest.builder()
                        .bucket(bucket)
                        .key(objectKey)
                        .build())
                .toExternalForm();
    }
}
