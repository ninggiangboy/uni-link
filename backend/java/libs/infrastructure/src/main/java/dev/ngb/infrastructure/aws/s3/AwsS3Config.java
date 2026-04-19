package dev.ngb.infrastructure.aws.s3;

import dev.ngb.application.port.config.StorageS3Config;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class AwsS3Config {

    private final StorageS3Config appConfig;

    private AwsBasicCredentials getCredentials() {
        return AwsBasicCredentials.create(
                appConfig.storageAccessKeyId(),
                appConfig.storageSecretAccessKey()
        );
    }

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = getCredentials();
        Region region = Region.of(appConfig.storageS3Region());
        String endpoint = appConfig.storageS3Endpoint();

        S3ClientBuilder builder = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(region)
                .forcePathStyle(true);

        if (endpoint != null && !endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint));
        }

        return builder.build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        AwsBasicCredentials credentials = getCredentials();
        Region region = Region.of(appConfig.storageS3Region());
        String endpoint = appConfig.storageS3Endpoint();

        S3Presigner.Builder builder = S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(region);

        if (endpoint != null && !endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint));
        }

        return builder.build();
    }
}
