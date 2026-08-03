package com.example.springbackendtemplate1.image.upload.serviceImpl;

import com.example.springbackendtemplate1.commons.imagehosting.ImageHostingConfigSource;
import com.example.springbackendtemplate1.image.upload.dto.response.ImageUploadResponse;
import com.example.springbackendtemplate1.image.upload.service.ImageUploadService;
import com.example.springbackendtemplate1.image.upload.strategy.ImageHostingStrategyRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ImageUploadServiceImpl implements ImageUploadService {

    private final ImageHostingStrategyRegistry registry;

    public ImageUploadServiceImpl(ImageHostingStrategyRegistry registry) {
        this.registry = registry;
    }

    @Override
    public ImageUploadResponse upload(MultipartFile file, ImageHostingConfigSource configSource) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file must not be empty");
        }
        String providerCode = configSource.getProviderCode();
        ImageUploadResponse response = registry.get(providerCode).upload(file, configSource.getConfig());
        log.info("Uploaded image via provider {} -> {}", providerCode, response.getImageUrl());
        return response;
    }

    @Override
    public List<ImageUploadResponse> uploadAll(List<MultipartFile> files, ImageHostingConfigSource configSource) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("At least one file must be provided");
        }

        List<ImageUploadResponse> uploaded = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                uploaded.add(upload(file, configSource));
            }
            return uploaded;
        } catch (Exception ex) {
            uploaded.forEach(response -> {
                try {
                    delete(response.getPublicId(), configSource);
                } catch (Exception deleteEx) {
                    log.error("Failed to roll back uploaded image {} after batch upload failure: {}",
                            response.getPublicId(), deleteEx.getMessage());
                }
            });
            throw ex;
        }
    }

    @Override
    public void delete(String publicId, ImageHostingConfigSource configSource) {
        String providerCode = configSource.getProviderCode();
        registry.get(providerCode).delete(publicId, configSource.getConfig());
        log.info("Deleted image with publicId {} via provider {}", publicId, providerCode);
    }
}
