package com.example.springbackendtemplate1.image.upload.strategy;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.springbackendtemplate1.image.upload.dto.response.ImageUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CloudinaryHostingStrategy implements ImageHostingStrategy {

    @Override
    public String providerCode() {
        return "CLOUDINARY";
    }

    @Override
    public ImageUploadResponse upload(MultipartFile file, Map<String, Object> config) {
        requireNonBlank(config, "cloudName", "apiKey", "apiSecret");

        Map<String, Object> uploadOptions = new HashMap<>();
        Object folder = config.get("folder");
        if (folder != null && !folder.toString().isBlank()) {
            uploadOptions.put("folder", folder);
        }

        try {
            Map<?, ?> result = buildCloudinary(config).uploader().upload(file.getBytes(), uploadOptions);
            return ImageUploadResponse.builder()
                    .imageUrl(result.get("secure_url").toString())
                    .publicId(result.get("public_id").toString())
                    .build();
        } catch (IOException ex) {
            log.error("Cloudinary upload failed for '{}': {}", file.getOriginalFilename(), ex.getMessage());
            throw new IllegalStateException("Cloudinary upload failed: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void delete(String publicId, Map<String, Object> config) {
        requireNonBlank(config, "cloudName", "apiKey", "apiSecret");
        try {
            buildCloudinary(config).uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException ex) {
            log.error("Cloudinary delete failed for publicId '{}': {}", publicId, ex.getMessage());
            throw new IllegalStateException("Cloudinary delete failed: " + ex.getMessage(), ex);
        }
    }

    private Cloudinary buildCloudinary(Map<String, Object> config) {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", config.get("cloudName"),
                "api_key", config.get("apiKey"),
                "api_secret", config.get("apiSecret"),
                "secure", true
        ));
    }

    private void requireNonBlank(Map<String, Object> config, String... keys) {
        for (String key : keys) {
            Object value = config == null ? null : config.get(key);
            if (value == null || value.toString().isBlank()) {
                throw new IllegalArgumentException("Missing required Cloudinary config key: " + key);
            }
        }
    }
}
