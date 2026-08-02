package com.example.springbackendtemplate1.image.hosting.controller;

import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingproviderconfig.CreateImageHostingProviderConfigRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingproviderconfig.ImageHostingProviderConfigFilterRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingproviderconfig.UpdateImageHostingProviderConfigRequest;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderConfigEntity;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderEntity;
import com.example.springbackendtemplate1.image.hosting.service.ImageHostingProviderConfigService;
import com.example.springbackendtemplate1.image.hosting.service.ImageHostingProviderService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/image-hosting-providers/{image-hosting-provider-id}/configs")
public class ImageHostingProviderConfigController {

    private final ImageHostingProviderConfigService imageHostingProviderConfigService;
    private final ImageHostingProviderService imageHostingProviderService;

    public ImageHostingProviderConfigController(ImageHostingProviderConfigService imageHostingProviderConfigService,
                                                 ImageHostingProviderService imageHostingProviderService) {
        this.imageHostingProviderConfigService = imageHostingProviderConfigService;
        this.imageHostingProviderService = imageHostingProviderService;
    }

    @PostMapping
    public ResponseEntity<?> create(
            @PathVariable("image-hosting-provider-id") Long imageHostingProviderId,
            @Valid @RequestBody CreateImageHostingProviderConfigRequest request) {
        ImageHostingProviderEntity providerEntity = imageHostingProviderService.getEntityById(imageHostingProviderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(imageHostingProviderConfigService.create(request, providerEntity));
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @PathVariable("image-hosting-provider-id") Long imageHostingProviderId,
            @Valid @ParameterObject ImageHostingProviderConfigFilterRequest request) {
        imageHostingProviderService.getEntityById(imageHostingProviderId);
        request.setImageHostingProviderId(imageHostingProviderId);
        return ResponseEntity.ok(imageHostingProviderConfigService.getAll(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable("image-hosting-provider-id") Long imageHostingProviderId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateImageHostingProviderConfigRequest request) {
        ImageHostingProviderConfigEntity entity = imageHostingProviderConfigService.getEntityById(imageHostingProviderId, id);
        return ResponseEntity.ok(imageHostingProviderConfigService.update(entity, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable("image-hosting-provider-id") Long imageHostingProviderId,
            @PathVariable Long id) {
        ImageHostingProviderConfigEntity entity = imageHostingProviderConfigService.getEntityById(imageHostingProviderId, id);
        return ResponseEntity.ok(imageHostingProviderConfigService.delete(entity));
    }
}
