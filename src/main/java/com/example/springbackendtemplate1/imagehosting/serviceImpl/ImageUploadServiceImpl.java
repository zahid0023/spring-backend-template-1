package com.example.springbackendtemplate1.imagehosting.serviceImpl;

import com.example.springbackendtemplate1.imagehosting.dto.request.ImageMetaRequest;
import com.example.springbackendtemplate1.imagehosting.dto.request.ImageRequest;
import com.example.springbackendtemplate1.imagehosting.dto.response.ImageUploadResponse;
import com.example.springbackendtemplate1.imagehosting.enums.ImageHostingProvider;
import com.example.springbackendtemplate1.imagehosting.service.ImageUploadService;
import com.example.springbackendtemplate1.imagehosting.strategy.ImageHostingStrategyRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageUploadServiceImpl implements ImageUploadService {

    private final ImageHostingStrategyRegistry registry;

    @Override
    public ImageUploadResponse upload(MultipartFile file, ImageHostingProvider provider, Map<String, String> config) {
        return registry.get(provider).upload(file, config);
    }

    @Override
    public void delete(String publicId, ImageHostingProvider provider, Map<String, String> config) {
        registry.get(provider).delete(publicId, config);
    }

    @Override
    public List<ImageRequest> uploadAll(List<MultipartFile> images,
                                        List<ImageMetaRequest> metaList,
                                        ImageHostingProvider provider,
                                        Map<String, String> config) {
        Map<String, ImageMetaRequest> metaMap = metaList.stream()
                .collect(Collectors.toMap(ImageMetaRequest::getClientImageId, Function.identity()));

        List<String> uploadedPublicIds = new ArrayList<>();
        try {
            return images.stream()
                    .map(image -> {
                        ImageMetaRequest meta = metaMap.get(image.getOriginalFilename());

                        if (meta == null) {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No metadata found for image: " + image.getOriginalFilename());
                        }

                        ImageUploadResponse response = upload(image, provider, config);

                        uploadedPublicIds.add(response.getPublicId());

                        return ImageRequest.builder()
                                .imageUrl(response.getImageUrl())
                                .publicId(response.getPublicId())
                                .caption(meta.getCaption())
                                .isDefault(meta.getIsDefault())
                                .sortOrder(meta.getSortOrder())
                                .build();
                    })
                    .toList();
        } catch (Exception ex) {
            uploadedPublicIds.forEach(publicId -> {
                try {
                    delete(publicId, provider, config);
                } catch (Exception deleteEx) {
                    log.error("Failed to delete uploaded image {} after upload failure: {}", publicId, deleteEx.getMessage());
                }
            });
            throw ex;
        }
    }
}
