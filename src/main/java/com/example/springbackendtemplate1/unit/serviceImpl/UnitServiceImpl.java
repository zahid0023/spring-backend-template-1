package com.example.springbackendtemplate1.unit.serviceImpl;

import com.example.springbackendtemplate1.unit.model.entity.UnitLocaleEntity;
import com.example.springbackendtemplate1.unit.model.mapper.UnitLocaleMapper;
import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.commons.utils.Pagination;
import com.example.springbackendtemplate1.unit.dto.request.unit.CreateUnitRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.UnitFilterRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.UpdateUnitRequest;
import com.example.springbackendtemplate1.unit.dto.response.units.UnitResponse;
import com.example.springbackendtemplate1.unit.model.dto.UnitDto;
import com.example.springbackendtemplate1.unit.model.dto.UnitTypeDto;
import com.example.springbackendtemplate1.unit.model.entity.UnitEntity;
import com.example.springbackendtemplate1.unit.model.enums.UnitSearchField;
import com.example.springbackendtemplate1.unit.model.enums.UnitSortField;
import com.example.springbackendtemplate1.unit.model.mapper.UnitMapper;
import com.example.springbackendtemplate1.unit.model.mapper.UnitTypeMapper;
import com.example.springbackendtemplate1.unit.repository.UnitRepository;
import com.example.springbackendtemplate1.commons.context.LocaleContext;
import com.example.springbackendtemplate1.unit.service.UnitService;
import com.example.springbackendtemplate1.unit.specification.UnitSpecification;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
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
public class UnitServiceImpl implements UnitService {

    private static final Set<String> ALLOWED_SORT_FIELDS = UnitSortField.allowedFields();
    private static final Set<String> ALLOWED_SEARCH_FIELDS = UnitSearchField.allowedFields();

    private final UnitRepository unitRepository;

    public UnitServiceImpl(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Transactional
    @Override
    public SuccessResponse create(CreateUnitRequest request, UnitTypeEntity unitTypeEntity, LocaleEntity localeEntity) {
        if (unitRepository.existsByCodeAndIsActiveAndIsDeleted(request.getCode(), true, false)) {
            throw new IllegalStateException("Unit with code '" + request.getCode() + "' already exists");
        }
        if (unitRepository.existsBySymbolAndIsActiveAndIsDeleted(request.getSymbol(), true, false)) {
            throw new IllegalStateException("Unit with symbol '" + request.getSymbol() + "' already exists");
        }

        UnitEntity entity = UnitMapper.create(request);
        unitTypeEntity.addUnitEntity(entity);

        UnitLocaleEntity unitLocaleEntity = UnitLocaleMapper.create(request.getLocale());
        localeEntity.addUnitLocaleEntity(unitLocaleEntity);
        entity.addUnitLocaleEntity(unitLocaleEntity);

        unitRepository.save(entity);
        log.info("Unit created with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public UnitEntity getEntityById(Long id) {
        return unitRepository.findByIdAndIsActiveAndIsDeleted(id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("Unit not found with id: " + id));
    }

    @Override
    public UnitResponse getById(Long id) {
        UnitEntity entity = getEntityById(id);
        UnitTypeDto unitType = UnitTypeMapper.toDto(entity.getUnitTypeEntity(), false).build();
        UnitDto dto = UnitMapper.toDto(entity, true)
                .unitType(unitType)
                .build();
        return new UnitResponse(dto);
    }

    @Override
    public PaginatedResponse<UnitDto> getAll(UnitFilterRequest request) {
        Specification<@NonNull UnitEntity> specification = UnitSpecification.filter(request, LocaleContext.getLocaleId());
        Pageable pageable = request.toPageable(ALLOWED_SORT_FIELDS, UnitSortField.localeSortFields());
        Page<@NonNull UnitDto> page = unitRepository
                .findAll(specification, pageable)
                .map(entity -> UnitMapper.toDto(entity, false)
                        .unitType(UnitTypeMapper.toDto(entity.getUnitTypeEntity(), false).build())
                        .build());
        return Pagination.buildPaginatedResponse(page, ALLOWED_SORT_FIELDS, ALLOWED_SEARCH_FIELDS);
    }

    @Transactional
    @Override
    public SuccessResponse update(UnitEntity entity, UpdateUnitRequest request) {
        if (!entity.getSymbol().equals(request.getSymbol())
                && unitRepository.existsBySymbolAndIsActiveAndIsDeleted(request.getSymbol(), true, false)) {
            throw new IllegalStateException("Unit with symbol '" + request.getSymbol() + "' already exists");
        }

        UnitMapper.update(entity, request);
        unitRepository.save(entity);
        log.info("Unit updated with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional
    @Override
    public SuccessResponse delete(UnitEntity entity) {
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        unitRepository.save(entity);
        log.info("Unit soft-deleted with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }
}
