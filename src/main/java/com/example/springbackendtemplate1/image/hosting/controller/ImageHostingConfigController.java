package com.example.springbackendtemplate1.image.hosting.controller;

import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingconfig.CreateImageHostingConfigRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingconfig.ImageHostingConfigFilterRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingconfig.UpdateImageHostingConfigRequest;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingConfigEntity;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderEntity;
import com.example.springbackendtemplate1.image.hosting.service.ImageHostingConfigService;
import com.example.springbackendtemplate1.image.hosting.service.ImageHostingProviderService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/image-hosting-configs")
public class ImageHostingConfigController {

    private final ImageHostingConfigService imageHostingConfigService;
    private final ImageHostingProviderService imageHostingProviderService;

    public ImageHostingConfigController(ImageHostingConfigService imageHostingConfigService,
                                        ImageHostingProviderService imageHostingProviderService) {
        this.imageHostingConfigService = imageHostingConfigService;
        this.imageHostingProviderService = imageHostingProviderService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateImageHostingConfigRequest request) {
        ImageHostingProviderEntity providerEntity = imageHostingProviderService.getEntityById(request.getImageHostingProviderId());
        return ResponseEntity.status(HttpStatus.CREATED).body(imageHostingConfigService.create(request, providerEntity));
    }

    @GetMapping
    public ResponseEntity<?> getAll(@Valid @ParameterObject ImageHostingConfigFilterRequest request) {
        return ResponseEntity.ok(imageHostingConfigService.getAll(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateImageHostingConfigRequest request) {
        ImageHostingConfigEntity entity = imageHostingConfigService.getEntityById(id);
        return ResponseEntity.ok(imageHostingConfigService.update(entity, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        ImageHostingConfigEntity entity = imageHostingConfigService.getEntityById(id);
        return ResponseEntity.ok(imageHostingConfigService.delete(entity));
    }
}
