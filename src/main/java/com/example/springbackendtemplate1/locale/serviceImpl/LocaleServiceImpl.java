package com.example.springbackendtemplate1.locale.serviceImpl;

import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.commons.utils.EntityValidator;
import com.example.springbackendtemplate1.commons.utils.Pagination;
import com.example.springbackendtemplate1.locale.dto.request.locale.CreateLocaleRequest;
import com.example.springbackendtemplate1.locale.dto.request.locale.LocaleFilterRequest;
import com.example.springbackendtemplate1.locale.dto.request.locale.UpdateLocaleRequest;
import com.example.springbackendtemplate1.locale.dto.response.locales.LocaleResponse;
import com.example.springbackendtemplate1.locale.model.dto.LocaleDto;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.locale.model.enums.LocaleSearchField;
import com.example.springbackendtemplate1.locale.model.enums.LocaleSortField;
import com.example.springbackendtemplate1.locale.model.mapper.LocaleMapper;
import com.example.springbackendtemplate1.locale.repository.LocaleRepository;
import com.example.springbackendtemplate1.locale.service.LocaleService;
import com.example.springbackendtemplate1.locale.specification.LocaleSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.jspecify.annotations.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class LocaleServiceImpl implements LocaleService {

    private static final Set<String> ALLOWED_SORT_FIELDS = LocaleSortField.allowedFields();
    private static final Set<String> ALLOWED_SEARCH_FIELDS = LocaleSearchField.allowedFields();

    private final LocaleRepository localeRepository;

    public LocaleServiceImpl(LocaleRepository localeRepository) {
        this.localeRepository = localeRepository;
    }

    @Transactional
    @Override
    public SuccessResponse create(CreateLocaleRequest request) {
        if (localeRepository.existsByCodeAndIsActiveAndIsDeleted(request.getCode(), true, false)) {
            throw new IllegalStateException("Locale with code '" + request.getCode() + "' already exists");
        }
        LocaleEntity entity = LocaleMapper.create(request);
        localeRepository.save(entity);
        log.info("Locale created with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public LocaleResponse getById(Long id) {
        LocaleEntity entity = getEntityById(id);
        LocaleDto dto = LocaleMapper.toDto(entity);
        return new LocaleResponse(dto);
    }

    @Override
    public PaginatedResponse<LocaleDto> getAll(LocaleFilterRequest request) {
        Page<@NonNull LocaleDto> page = localeRepository
                .findAll(LocaleSpecification.filter(request), request.toPageable(ALLOWED_SORT_FIELDS))
                .map(LocaleMapper::toDto);
        return Pagination.buildPaginatedResponse(page, ALLOWED_SORT_FIELDS, ALLOWED_SEARCH_FIELDS);
    }

    @Transactional
    @Override
    public SuccessResponse update(LocaleEntity entity, UpdateLocaleRequest request) {
        LocaleMapper.update(entity, request);
        localeRepository.save(entity);
        log.info("Locale updated with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional
    @Override
    public SuccessResponse delete(LocaleEntity entity) {
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        localeRepository.save(entity);
        log.info("Locale soft-deleted with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public LocaleEntity getEntityById(Long id) {
        return localeRepository.findByIdAndIsActiveAndIsDeleted(id, true, false)
                .orElseThrow(() -> {
                    log.warn("Locale not found with id: {}", id);
                    return new EntityNotFoundException("Locale not found with id: " + id);
                });
    }

    @Override
    public List<LocaleEntity> getAll(Set<Long> ids) {
        List<LocaleEntity> localeEntities = localeRepository.findAllByIdInAndIsActiveAndIsDeleted(ids, true, false);
        EntityValidator.validateAllFound(ids, localeEntities, LocaleEntity::getId, "Locale");
        return localeEntities;
    }
}
