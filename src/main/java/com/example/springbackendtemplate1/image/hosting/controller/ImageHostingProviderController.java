package com.example.springbackendtemplate1.image.hosting.controller;

import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.CreateImageHostingProviderRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.ImageHostingProviderFilterRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.UpdateImageHostingProviderRequest;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderEntity;
import com.example.springbackendtemplate1.image.hosting.service.ImageHostingProviderService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/image-hosting-providers")
public class ImageHostingProviderController {

    private final ImageHostingProviderService imageHostingProviderService;

    public ImageHostingProviderController(ImageHostingProviderService imageHostingProviderService) {
        this.imageHostingProviderService = imageHostingProviderService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateImageHostingProviderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(imageHostingProviderService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(imageHostingProviderService.getById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAll(@Valid @ParameterObject ImageHostingProviderFilterRequest request) {
        return ResponseEntity.ok(imageHostingProviderService.getAll(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateImageHostingProviderRequest request) {
        ImageHostingProviderEntity entity = imageHostingProviderService.getEntityById(id);
        return ResponseEntity.ok(imageHostingProviderService.update(entity, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        ImageHostingProviderEntity entity = imageHostingProviderService.getEntityById(id);
        return ResponseEntity.ok(imageHostingProviderService.delete(entity));
    }
}
