package com.example.springbackendtemplate1.image.hosting.serviceImpl;

import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.configfield.CreateImageHostingProviderConfigFieldRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.configfield.UpdateImageHostingProviderConfigFieldRequest;
import com.example.springbackendtemplate1.image.hosting.model.dto.ImageHostingProviderConfigFieldDto;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderConfigFieldEntity;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderEntity;
import com.example.springbackendtemplate1.image.hosting.model.mapper.ImageHostingProviderConfigFieldMapper;
import com.example.springbackendtemplate1.image.hosting.repository.ImageHostingProviderConfigFieldRepository;
import com.example.springbackendtemplate1.image.hosting.service.ImageHostingProviderConfigFieldService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class ImageHostingProviderConfigFieldServiceImpl implements ImageHostingProviderConfigFieldService {

    private final ImageHostingProviderConfigFieldRepository imageHostingProviderConfigFieldRepository;

    public ImageHostingProviderConfigFieldServiceImpl(
            ImageHostingProviderConfigFieldRepository imageHostingProviderConfigFieldRepository) {
        this.imageHostingProviderConfigFieldRepository = imageHostingProviderConfigFieldRepository;
    }

    @Transactional
    @Override
    public SuccessResponse create(CreateImageHostingProviderConfigFieldRequest request, ImageHostingProviderEntity providerEntity) {
        if (imageHostingProviderConfigFieldRepository.existsByImageHostingProviderEntity_IdAndKeyAndIsActiveAndIsDeleted(
                providerEntity.getId(), request.getKey(), true, false)) {
            throw new IllegalStateException("Config field with key '" + request.getKey() + "' already exists for this provider");
        }

        ImageHostingProviderConfigFieldEntity entity = ImageHostingProviderConfigFieldMapper.create(request);
        providerEntity.addImageHostingProviderConfigFieldEntity(entity);
        imageHostingProviderConfigFieldRepository.save(entity);
        log.info("ImageHostingProviderConfigField created with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public ImageHostingProviderConfigFieldEntity getEntityById(Long imageHostingProviderId, Long id) {
        return imageHostingProviderConfigFieldRepository
                .findByImageHostingProviderEntity_IdAndIdAndIsActiveAndIsDeleted(imageHostingProviderId, id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("ImageHostingProviderConfigField not found with id: " + id));
    }

    @Override
    public List<ImageHostingProviderConfigFieldDto> getAll(Long imageHostingProviderId) {
        return imageHostingProviderConfigFieldRepository
                .findByImageHostingProviderEntity_IdAndIsActiveAndIsDeleted(imageHostingProviderId, true, false)
                .stream()
                .map(entity -> ImageHostingProviderConfigFieldMapper.toDto(entity).build())
                .toList();
    }

    @Transactional
    @Override
    public SuccessResponse update(ImageHostingProviderConfigFieldEntity entity, UpdateImageHostingProviderConfigFieldRequest request) {
        ImageHostingProviderConfigFieldMapper.update(entity, request);
        imageHostingProviderConfigFieldRepository.save(entity);
        log.info("ImageHostingProviderConfigField updated with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional
    @Override
    public SuccessResponse delete(ImageHostingProviderConfigFieldEntity entity) {
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        imageHostingProviderConfigFieldRepository.save(entity);
        log.info("ImageHostingProviderConfigField soft-deleted with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }
}
