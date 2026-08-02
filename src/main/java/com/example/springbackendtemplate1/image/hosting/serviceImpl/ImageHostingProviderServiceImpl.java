package com.example.springbackendtemplate1.image.hosting.serviceImpl;

import com.example.springbackendtemplate1.commons.context.LocaleContext;
import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.commons.utils.Pagination;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.CreateImageHostingProviderRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.ImageHostingProviderFilterRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.UpdateImageHostingProviderRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.configfield.CreateImageHostingProviderConfigFieldRequest;
import com.example.springbackendtemplate1.image.hosting.dto.response.imagehostingproviders.ImageHostingProviderResponse;
import com.example.springbackendtemplate1.image.hosting.model.dto.ImageHostingProviderDto;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderEntity;
import com.example.springbackendtemplate1.image.hosting.model.enums.ImageHostingProviderSearchField;
import com.example.springbackendtemplate1.image.hosting.model.enums.ImageHostingProviderSortField;
import com.example.springbackendtemplate1.image.hosting.model.mapper.ImageHostingProviderConfigFieldMapper;
import com.example.springbackendtemplate1.image.hosting.model.mapper.ImageHostingProviderMapper;
import com.example.springbackendtemplate1.image.hosting.repository.ImageHostingProviderRepository;
import com.example.springbackendtemplate1.image.hosting.service.ImageHostingProviderService;
import com.example.springbackendtemplate1.image.hosting.specification.ImageHostingProviderSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class ImageHostingProviderServiceImpl implements ImageHostingProviderService {

    private static final Set<String> ALLOWED_SORT_FIELDS = ImageHostingProviderSortField.allowedFields();
    private static final Set<String> ALLOWED_SEARCH_FIELDS = ImageHostingProviderSearchField.allowedFields();

    private final ImageHostingProviderRepository imageHostingProviderRepository;

    public ImageHostingProviderServiceImpl(ImageHostingProviderRepository imageHostingProviderRepository) {
        this.imageHostingProviderRepository = imageHostingProviderRepository;
    }

    @Transactional
    @Override
    public SuccessResponse create(CreateImageHostingProviderRequest request) {
        if (imageHostingProviderRepository.existsByCodeAndIsActiveAndIsDeleted(request.getCode(), true, false)) {
            throw new IllegalStateException("ImageHostingProvider with code '" + request.getCode() + "' already exists");
        }

        Set<String> seenKeys = new HashSet<>();
        for (CreateImageHostingProviderConfigFieldRequest fieldRequest : request.getConfigFields()) {
            if (!seenKeys.add(fieldRequest.getKey())) {
                throw new IllegalStateException("Duplicate config field key '" + fieldRequest.getKey() + "' in request");
            }
        }

        ImageHostingProviderEntity entity = ImageHostingProviderMapper.create(request);
        for (CreateImageHostingProviderConfigFieldRequest fieldRequest : request.getConfigFields()) {
            entity.addImageHostingProviderConfigFieldEntity(ImageHostingProviderConfigFieldMapper.create(fieldRequest));
        }
        imageHostingProviderRepository.save(entity);
        log.info("ImageHostingProvider created with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public ImageHostingProviderEntity getEntityById(Long id) {
        return imageHostingProviderRepository.findByIdAndIsActiveAndIsDeleted(id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("ImageHostingProvider not found with id: " + id));
    }

    @Override
    public ImageHostingProviderResponse getById(Long id) {
        ImageHostingProviderEntity entity = getEntityById(id);
        ImageHostingProviderDto dto = ImageHostingProviderMapper.toDto(entity).build();
        return new ImageHostingProviderResponse(dto);
    }

    @Override
    public PaginatedResponse<ImageHostingProviderDto> getAll(ImageHostingProviderFilterRequest request) {
        Specification<@NonNull ImageHostingProviderEntity> specification =
                ImageHostingProviderSpecification.filter(request, LocaleContext.getLocaleId());
        Pageable pageable = request.toPageable(ALLOWED_SORT_FIELDS);
        Page<@NonNull ImageHostingProviderDto> page = imageHostingProviderRepository
                .findAll(specification, pageable)
                .map(entity -> ImageHostingProviderMapper.toDto(entity).build());
        return Pagination.buildPaginatedResponse(page, ALLOWED_SORT_FIELDS, ALLOWED_SEARCH_FIELDS);
    }

    @Transactional
    @Override
    public SuccessResponse update(ImageHostingProviderEntity entity, UpdateImageHostingProviderRequest request) {
        ImageHostingProviderMapper.update(entity, request);
        imageHostingProviderRepository.save(entity);
        log.info("ImageHostingProvider updated with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional
    @Override
    public SuccessResponse delete(ImageHostingProviderEntity entity) {
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        imageHostingProviderRepository.save(entity);
        log.info("ImageHostingProvider soft-deleted with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }
}
