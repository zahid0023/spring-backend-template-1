package com.example.springbackendtemplate1.imagehosting.strategy;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.springbackendtemplate1.imagehosting.dto.response.ImageUploadResponse;
import com.example.springbackendtemplate1.imagehosting.enums.ImageHostingProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class CloudinaryHostingStrategy implements ImageHostingStrategy {

    @Override
    public ImageHostingProvider provider() {
        return ImageHostingProvider.CLOUDINARY;
    }

    @Override
    public ImageUploadResponse upload(MultipartFile file, Map<String, String> config) {
        provider().validate(config);
        try {
            Map uploadResult = buildCloudinary(config).uploader().upload(
                    file.getBytes(),
                    ObjectUtils.emptyMap()
            );
            return ImageUploadResponse.builder()
                    .imageUrl(uploadResult.get("secure_url").toString())
                    .publicId(uploadResult.get("public_id").toString())
                    .build();
        } catch (IOException ex) {
            log.error("Cloudinary upload failed for '{}': {}", file.getOriginalFilename(), ex.getMessage());
            throw new IllegalStateException("Cloudinary upload failed: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void delete(String publicId, Map<String, String> config) {
        provider().validate(config);
        try {
            buildCloudinary(config).uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException ex) {
            log.error("Cloudinary delete failed for publicId '{}': {}", publicId, ex.getMessage());
            throw new IllegalStateException("Cloudinary delete failed: " + ex.getMessage(), ex);
        }
    }

    private Cloudinary buildCloudinary(Map<String, String> config) {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", config.get("cloud_name"),
                "api_key", config.get("api_key"),
                "api_secret", config.get("api_secret"),
                "secure", true
        ));
    }
}
