package com.example.springbackendtemplate1.imagehosting.service;

import com.example.springbackendtemplate1.imagehosting.dto.request.ImageMetaRequest;
import com.example.springbackendtemplate1.imagehosting.dto.request.ImageRequest;
import com.example.springbackendtemplate1.imagehosting.dto.response.ImageUploadResponse;
import com.example.springbackendtemplate1.imagehosting.enums.ImageHostingProvider;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ImageUploadService {
    ImageUploadResponse upload(MultipartFile file,
                               ImageHostingProvider provider,
                               Map<String, String> config);

    void delete(String publicId,
                ImageHostingProvider provider,
                Map<String, String> config);

    List<ImageRequest> uploadAll(List<MultipartFile> images,
                                 List<ImageMetaRequest> metaList,
                                 ImageHostingProvider provider,
                                 Map<String, String> config);
}
