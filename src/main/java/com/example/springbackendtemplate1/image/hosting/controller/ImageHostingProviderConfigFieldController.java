package com.example.springbackendtemplate1.image.hosting.controller;

import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.configfield.CreateImageHostingProviderConfigFieldRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.configfield.UpdateImageHostingProviderConfigFieldRequest;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderConfigFieldEntity;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderEntity;
import com.example.springbackendtemplate1.image.hosting.service.ImageHostingProviderConfigFieldService;
import com.example.springbackendtemplate1.image.hosting.service.ImageHostingProviderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/image-hosting-providers/{image-hosting-provider-id}/config-fields")
public class ImageHostingProviderConfigFieldController {

    private final ImageHostingProviderService imageHostingProviderService;
    private final ImageHostingProviderConfigFieldService imageHostingProviderConfigFieldService;

    public ImageHostingProviderConfigFieldController(
            ImageHostingProviderService imageHostingProviderService,
            ImageHostingProviderConfigFieldService imageHostingProviderConfigFieldService) {
        this.imageHostingProviderService = imageHostingProviderService;
        this.imageHostingProviderConfigFieldService = imageHostingProviderConfigFieldService;
    }

    @PostMapping
    public ResponseEntity<?> create(
            @PathVariable("image-hosting-provider-id") Long imageHostingProviderId,
            @Valid @RequestBody CreateImageHostingProviderConfigFieldRequest request) {
        ImageHostingProviderEntity providerEntity = imageHostingProviderService.getEntityById(imageHostingProviderId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(imageHostingProviderConfigFieldService.create(request, providerEntity));
    }

    @GetMapping
    public ResponseEntity<?> getAll(@PathVariable("image-hosting-provider-id") Long imageHostingProviderId) {
        imageHostingProviderService.getEntityById(imageHostingProviderId);
        return ResponseEntity.ok(imageHostingProviderConfigFieldService.getAll(imageHostingProviderId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable("image-hosting-provider-id") Long imageHostingProviderId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateImageHostingProviderConfigFieldRequest request) {
        ImageHostingProviderConfigFieldEntity entity = imageHostingProviderConfigFieldService.getEntityById(imageHostingProviderId, id);
        return ResponseEntity.ok(imageHostingProviderConfigFieldService.update(entity, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable("image-hosting-provider-id") Long imageHostingProviderId,
            @PathVariable Long id) {
        ImageHostingProviderConfigFieldEntity entity = imageHostingProviderConfigFieldService.getEntityById(imageHostingProviderId, id);
        return ResponseEntity.ok(imageHostingProviderConfigFieldService.delete(entity));
    }
}
