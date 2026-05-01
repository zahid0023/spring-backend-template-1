package com.example.springbackendtemplate1.imagehosting.strategy;

import com.example.springbackendtemplate1.imagehosting.dto.response.ImageUploadResponse;
import com.example.springbackendtemplate1.imagehosting.enums.ImageHostingProvider;
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
    public ImageHostingProvider provider() {
        return ImageHostingProvider.S3;
    }

    @Override
    public ImageUploadResponse upload(MultipartFile file, Map<String, String> config) {
        provider().validate(config);

        String bucket = config.get("bucket");
        String region = config.get("region");
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        try (S3Client s3Client = buildS3Client(config)) {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException ex) {
            log.error("S3 upload failed for '{}': {}", file.getOriginalFilename(), ex.getMessage());
            throw new IllegalStateException("S3 upload failed: " + ex.getMessage(), ex);
        }

        String url = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, fileName);
        return ImageUploadResponse.builder()
                .imageUrl(url)
                .publicId(fileName)
                .build();
    }

    @Override
    public void delete(String publicId, Map<String, String> config) {
        provider().validate(config);
        try (S3Client s3Client = buildS3Client(config)) {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(config.get("bucket"))
                    .key(publicId)
                    .build());
        }
    }

    private S3Client buildS3Client(Map<String, String> config) {
        return S3Client.builder()
                .region(Region.of(config.get("region")))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(config.get("access_key"), config.get("secret_key"))))
                .build();
    }
}
