package com.example.springbackendtemplate1.image.upload.strategy;

import com.example.springbackendtemplate1.image.upload.dto.response.ImageUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ImageHostingStrategy {

    String providerCode();

    ImageUploadResponse upload(MultipartFile file, Map<String, Object> config);

    void delete(String publicId, Map<String, Object> config);
}
