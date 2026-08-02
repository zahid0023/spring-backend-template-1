package com.example.springbackendtemplate1.image.hosting.serviceImpl;

import com.example.springbackendtemplate1.commons.context.LocaleContext;
import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.commons.utils.Pagination;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingconfig.CreateImageHostingConfigRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingconfig.ImageHostingConfigFilterRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingconfig.UpdateImageHostingConfigRequest;
import com.example.springbackendtemplate1.image.hosting.model.dto.ImageHostingConfigDto;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingConfigEntity;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderEntity;
import com.example.springbackendtemplate1.image.hosting.model.enums.ImageHostingConfigSearchField;
import com.example.springbackendtemplate1.image.hosting.model.enums.ImageHostingConfigSortField;
import com.example.springbackendtemplate1.image.hosting.model.mapper.ImageHostingConfigMapper;
import com.example.springbackendtemplate1.image.hosting.repository.ImageHostingConfigRepository;
import com.example.springbackendtemplate1.image.hosting.service.ImageHostingConfigService;
import com.example.springbackendtemplate1.image.hosting.specification.ImageHostingConfigSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Slf4j
public class ImageHostingConfigServiceImpl implements ImageHostingConfigService {

    private static final Set<String> ALLOWED_SORT_FIELDS = ImageHostingConfigSortField.allowedFields();
    private static final Set<String> ALLOWED_SEARCH_FIELDS = ImageHostingConfigSearchField.allowedFields();

    private final ImageHostingConfigRepository imageHostingConfigRepository;

    public ImageHostingConfigServiceImpl(ImageHostingConfigRepository imageHostingConfigRepository) {
        this.imageHostingConfigRepository = imageHostingConfigRepository;
    }

    @Transactional
    @Override
    public SuccessResponse create(CreateImageHostingConfigRequest request, ImageHostingProviderEntity providerEntity) {
        if (imageHostingConfigRepository.existsByImageHostingProviderEntity_IdAndNameAndIsActiveAndIsDeleted(
                providerEntity.getId(), request.getName(), true, false)) {
            throw new IllegalStateException("ImageHostingConfig with name '" + request.getName() + "' already exists for this provider");
        }

        ImageHostingConfigEntity entity = ImageHostingConfigMapper.create(request);
        providerEntity.addImageHostingConfigEntity(entity);
        imageHostingConfigRepository.save(entity);
        log.info("ImageHostingConfig created with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public ImageHostingConfigEntity getEntityById(Long id) {
        return imageHostingConfigRepository.findByIdAndIsActiveAndIsDeleted(id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("ImageHostingConfig not found with id: " + id));
    }

    @Override
    public PaginatedResponse<ImageHostingConfigDto> getAll(ImageHostingConfigFilterRequest request) {
        Specification<@NonNull ImageHostingConfigEntity> specification =
                ImageHostingConfigSpecification.filter(request, LocaleContext.getLocaleId());
        Pageable pageable = request.toPageable(ALLOWED_SORT_FIELDS);
        Page<@NonNull ImageHostingConfigDto> page = imageHostingConfigRepository
                .findAll(specification, pageable)
                .map(entity -> ImageHostingConfigMapper.toDto(entity).build());
        return Pagination.buildPaginatedResponse(page, ALLOWED_SORT_FIELDS, ALLOWED_SEARCH_FIELDS);
    }

    @Transactional
    @Override
    public SuccessResponse update(ImageHostingConfigEntity entity, UpdateImageHostingConfigRequest request) {
        if (imageHostingConfigRepository.existsByImageHostingProviderEntity_IdAndNameAndIdNotAndIsActiveAndIsDeleted(
                entity.getImageHostingProviderEntity().getId(), request.getName(), entity.getId(), true, false)) {
            throw new IllegalStateException("ImageHostingConfig with name '" + request.getName() + "' already exists for this provider");
        }

        ImageHostingConfigMapper.update(entity, request);
        imageHostingConfigRepository.save(entity);
        log.info("ImageHostingConfig updated with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional
    @Override
    public SuccessResponse delete(ImageHostingConfigEntity entity) {
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        imageHostingConfigRepository.save(entity);
        log.info("ImageHostingConfig soft-deleted with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }
}
