package com.example.springbackendtemplate1.address.controller;

import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.address.service.CountryService;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderConfigEntity;
import com.example.springbackendtemplate1.image.hosting.service.ImageHostingProviderConfigService;
import com.example.springbackendtemplate1.image.upload.dto.response.ImageUploadResponse;
import com.example.springbackendtemplate1.image.upload.service.ImageUploadService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/countries/{country-id}/images")
public class CountryImageController {

    private final CountryService countryService;
    private final ImageHostingProviderConfigService imageHostingProviderConfigService;
    private final ImageUploadService imageUploadService;

    public CountryImageController(CountryService countryService,
                                  ImageHostingProviderConfigService imageHostingProviderConfigService,
                                  ImageUploadService imageUploadService) {
        this.countryService = countryService;
        this.imageHostingProviderConfigService = imageHostingProviderConfigService;
        this.imageUploadService = imageUploadService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(
            @PathVariable("country-id") Long countryId,
            @RequestParam("provider_config_id") Long providerConfigId,
            @RequestPart("image") MultipartFile image) {
        validateSvg(image);
        CountryEntity countryEntity = countryService.getEntityById(countryId);
        ImageHostingProviderConfigEntity configEntity = imageHostingProviderConfigService.getEntityById(providerConfigId);
        ImageUploadResponse uploadResponse = imageUploadService.upload(image, configEntity);
        return ResponseEntity.ok(countryService.updateFlagImage(countryEntity, uploadResponse.getImageUrl()));
    }

    private void validateSvg(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        boolean isSvgContentType = "image/svg+xml".equalsIgnoreCase(contentType);
        boolean isSvgFilename = filename != null && filename.toLowerCase().endsWith(".svg");
        if (!isSvgContentType && !isSvgFilename) {
            throw new IllegalArgumentException("Country flag image must be an SVG file");
        }
    }

    @DeleteMapping
    public ResponseEntity<?> delete(
            @PathVariable("country-id") Long countryId,
            @RequestParam("provider_config_id") Long providerConfigId,
            @RequestParam("public_id") String publicId) {
        CountryEntity countryEntity = countryService.getEntityById(countryId);
        ImageHostingProviderConfigEntity configEntity = imageHostingProviderConfigService.getEntityById(providerConfigId);
        imageUploadService.delete(publicId, configEntity);
        return ResponseEntity.ok(countryService.updateFlagImage(countryEntity, ""));
    }
}
