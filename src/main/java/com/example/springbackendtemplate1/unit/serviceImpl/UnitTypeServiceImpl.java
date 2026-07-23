package com.example.springbackendtemplate1.unit.serviceImpl;

import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.commons.utils.EntityValidator;
import com.example.springbackendtemplate1.commons.utils.Pagination;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.unit.dto.request.unittype.CreateUnitTypeRequest;
import com.example.springbackendtemplate1.unit.dto.request.unittype.UnitTypeFilterRequest;
import com.example.springbackendtemplate1.unit.dto.request.unittype.UpdateUnitTypeRequest;
import com.example.springbackendtemplate1.unit.dto.response.unittypes.UnitTypeResponse;
import com.example.springbackendtemplate1.unit.model.dto.UnitTypeDto;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeEntity;
import com.example.springbackendtemplate1.unit.model.enums.UnitTypeSearchField;
import com.example.springbackendtemplate1.unit.model.enums.UnitTypeSortField;
import com.example.springbackendtemplate1.unit.model.mapper.UnitTypeMapper;
import com.example.springbackendtemplate1.unit.repository.UnitTypeRepository;
import com.example.springbackendtemplate1.unit.service.UnitTypeService;
import com.example.springbackendtemplate1.unit.specification.UnitTypeSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class UnitTypeServiceImpl implements UnitTypeService {

    private static final Set<String> ALLOWED_SORT_FIELDS = UnitTypeSortField.allowedFields();
    private static final Set<String> ALLOWED_SEARCH_FIELDS = UnitTypeSearchField.allowedFields();

    private final UnitTypeRepository unitTypeRepository;

    public UnitTypeServiceImpl(UnitTypeRepository unitTypeRepository) {
        this.unitTypeRepository = unitTypeRepository;
    }

    @Transactional
    @Override
    public SuccessResponse create(CreateUnitTypeRequest request, Map<Long, LocaleEntity> localeEntityMap) {
        UnitTypeEntity entity = UnitTypeMapper.create(request, localeEntityMap);
        unitTypeRepository.save(entity);
        log.info("UnitType created with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public UnitTypeResponse getById(Long id) {
        UnitTypeEntity entity = getEntityById(id);
        UnitTypeDto dto = UnitTypeMapper.toDto(entity);
        return new UnitTypeResponse(dto);
    }

    @Override
    public PaginatedResponse<UnitTypeDto> getAll(UnitTypeFilterRequest request) {
        Page<@NonNull UnitTypeDto> page = unitTypeRepository
                .findAll(UnitTypeSpecification.filter(request), request.toPageable(ALLOWED_SORT_FIELDS))
                .map(UnitTypeMapper::toDto);
        return Pagination.buildPaginatedResponse(page, ALLOWED_SORT_FIELDS, ALLOWED_SEARCH_FIELDS);
    }

    @Transactional
    @Override
    public SuccessResponse update(UnitTypeEntity entity, UpdateUnitTypeRequest request) {
        UnitTypeMapper.update(entity, request);
        unitTypeRepository.save(entity);
        log.info("UnitType updated with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional
    @Override
    public SuccessResponse delete(Long id) {
        UnitTypeEntity entity = getEntityById(id);
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        unitTypeRepository.save(entity);
        log.info("UnitType soft-deleted with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public UnitTypeEntity getEntityById(Long id) {
        return unitTypeRepository.findByIdAndIsActiveAndIsDeleted(id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("UnitType not found with id: " + id));
    }

    @Override
    public List<UnitTypeEntity> getAll(Set<Long> ids) {
        List<UnitTypeEntity> entities = unitTypeRepository.findAllByIdInAndIsActiveAndIsDeleted(ids, true, false);
        EntityValidator.validateAllFound(ids, entities, UnitTypeEntity::getId, "UnitType");
        return entities;
    }
}
