package com.example.springbackendtemplate1.image.hosting.serviceImpl;

import com.example.springbackendtemplate1.commons.context.LocaleContext;
import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.commons.utils.Pagination;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingproviderconfig.CreateImageHostingProviderConfigRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingproviderconfig.ImageHostingProviderConfigFilterRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingproviderconfig.UpdateImageHostingProviderConfigRequest;
import com.example.springbackendtemplate1.image.hosting.model.dto.ImageHostingProviderConfigDto;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderConfigEntity;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderEntity;
import com.example.springbackendtemplate1.image.hosting.model.enums.ImageHostingProviderConfigSearchField;
import com.example.springbackendtemplate1.image.hosting.model.enums.ImageHostingProviderConfigSortField;
import com.example.springbackendtemplate1.image.hosting.model.mapper.ImageHostingProviderConfigMapper;
import com.example.springbackendtemplate1.image.hosting.repository.ImageHostingProviderConfigRepository;
import com.example.springbackendtemplate1.image.hosting.service.ImageHostingProviderConfigService;
import com.example.springbackendtemplate1.image.hosting.specification.ImageHostingProviderConfigSpecification;
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
public class ImageHostingProviderConfigServiceImpl implements ImageHostingProviderConfigService {

    private static final Set<String> ALLOWED_SORT_FIELDS = ImageHostingProviderConfigSortField.allowedFields();
    private static final Set<String> ALLOWED_SEARCH_FIELDS = ImageHostingProviderConfigSearchField.allowedFields();

    private final ImageHostingProviderConfigRepository imageHostingProviderConfigRepository;

    public ImageHostingProviderConfigServiceImpl(ImageHostingProviderConfigRepository imageHostingProviderConfigRepository) {
        this.imageHostingProviderConfigRepository = imageHostingProviderConfigRepository;
    }

    @Transactional
    @Override
    public SuccessResponse create(CreateImageHostingProviderConfigRequest request, ImageHostingProviderEntity providerEntity) {
        if (imageHostingProviderConfigRepository.existsByImageHostingProviderEntity_IdAndNameAndIsActiveAndIsDeleted(
                providerEntity.getId(), request.getName(), true, false)) {
            throw new IllegalStateException("ImageHostingProviderConfig with name '" + request.getName() + "' already exists for this provider");
        }

        ImageHostingProviderConfigEntity entity = ImageHostingProviderConfigMapper.create(request);
        providerEntity.addImageHostingProviderConfigEntity(entity);
        imageHostingProviderConfigRepository.save(entity);
        log.info("ImageHostingProviderConfig created with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public ImageHostingProviderConfigEntity getEntityById(Long imageHostingProviderId, Long id) {
        return imageHostingProviderConfigRepository
                .findByImageHostingProviderEntity_IdAndIdAndIsActiveAndIsDeleted(imageHostingProviderId, id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("ImageHostingProviderConfig not found with id: " + id));
    }

    @Override
    public PaginatedResponse<ImageHostingProviderConfigDto> getAll(ImageHostingProviderConfigFilterRequest request) {
        Specification<@NonNull ImageHostingProviderConfigEntity> specification =
                ImageHostingProviderConfigSpecification.filter(request, LocaleContext.getLocaleId());
        Pageable pageable = request.toPageable(ALLOWED_SORT_FIELDS);
        Page<@NonNull ImageHostingProviderConfigDto> page = imageHostingProviderConfigRepository
                .findAll(specification, pageable)
                .map(entity -> ImageHostingProviderConfigMapper.toDto(entity).build());
        return Pagination.buildPaginatedResponse(page, ALLOWED_SORT_FIELDS, ALLOWED_SEARCH_FIELDS);
    }

    @Transactional
    @Override
    public SuccessResponse update(ImageHostingProviderConfigEntity entity, UpdateImageHostingProviderConfigRequest request) {
        if (imageHostingProviderConfigRepository.existsByImageHostingProviderEntity_IdAndNameAndIdNotAndIsActiveAndIsDeleted(
                entity.getImageHostingProviderEntity().getId(), request.getName(), entity.getId(), true, false)) {
            throw new IllegalStateException("ImageHostingProviderConfig with name '" + request.getName() + "' already exists for this provider");
        }

        ImageHostingProviderConfigMapper.update(entity, request);
        imageHostingProviderConfigRepository.save(entity);
        log.info("ImageHostingProviderConfig updated with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional
    @Override
    public SuccessResponse delete(ImageHostingProviderConfigEntity entity) {
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        imageHostingProviderConfigRepository.save(entity);
        log.info("ImageHostingProviderConfig soft-deleted with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }
}
