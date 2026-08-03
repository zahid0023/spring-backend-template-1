package com.example.springbackendtemplate1.image.upload.strategy;

import com.example.springbackendtemplate1.image.upload.dto.response.ImageUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class S3HostingStrategy implements ImageHostingStrategy {

    @Override
    public String providerCode() {
        return "AWS_S3";
    }

    @Override
    public ImageUploadResponse upload(MultipartFile file, Map<String, Object> config) {
        requireNonBlank(config, "bucket", "region", "accessKey", "secretKey");

        String bucket = config.get("bucket").toString();
        String region = config.get("region").toString();
        String key = UUID.randomUUID() + "-" + file.getOriginalFilename();

        try (S3Client s3Client = buildS3Client(config)) {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException ex) {
            log.error("S3 upload failed for '{}': {}", file.getOriginalFilename(), ex.getMessage());
            throw new IllegalStateException("S3 upload failed: " + ex.getMessage(), ex);
        }

        String url = "https://%s.s3.%s.amazonaws.com/%s".formatted(bucket, region, key);
        return ImageUploadResponse.builder()
                .imageUrl(url)
                .publicId(key)
                .build();
    }

    @Override
    public void delete(String publicId, Map<String, Object> config) {
        requireNonBlank(config, "bucket", "region", "accessKey", "secretKey");
        try (S3Client s3Client = buildS3Client(config)) {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(config.get("bucket").toString())
                    .key(publicId)
                    .build());
        }
    }

    private S3Client buildS3Client(Map<String, Object> config) {
        return S3Client.builder()
                .region(Region.of(config.get("region").toString()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                config.get("accessKey").toString(),
                                config.get("secretKey").toString())))
                .build();
    }

    private void requireNonBlank(Map<String, Object> config, String... keys) {
        for (String key : keys) {
            Object value = config == null ? null : config.get(key);
            if (value == null || value.toString().isBlank()) {
                throw new IllegalArgumentException("Missing required S3 config key: " + key);
            }
        }
    }
}
