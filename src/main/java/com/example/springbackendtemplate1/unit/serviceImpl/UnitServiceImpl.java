package com.example.springbackendtemplate1.unit.serviceImpl;

import com.example.springbackendtemplate1.unit.dto.request.unit.CreateUnitRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.UnitFilterRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.UpdateUnitRequest;
import com.example.springbackendtemplate1.unit.dto.response.units.UnitResponse;
import com.example.springbackendtemplate1.unit.model.dto.UnitDto;
import com.example.springbackendtemplate1.unit.model.entity.UnitEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitLocaleEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeEntity;
import com.example.springbackendtemplate1.unit.model.enums.UnitSearchField;
import com.example.springbackendtemplate1.unit.model.enums.UnitSortField;
import com.example.springbackendtemplate1.unit.model.mapper.UnitLocaleMapper;
import com.example.springbackendtemplate1.unit.model.mapper.UnitMapper;
import com.example.springbackendtemplate1.unit.repository.UnitRepository;
import com.example.springbackendtemplate1.unit.service.UnitService;
import com.example.springbackendtemplate1.unit.specification.UnitSpecification;
import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.commons.utils.EntityValidator;
import com.example.springbackendtemplate1.commons.utils.Pagination;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
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
public class UnitServiceImpl implements UnitService {

    private static final Set<String> ALLOWED_SORT_FIELDS = UnitSortField.allowedFields();
    private static final Set<String> ALLOWED_SEARCH_FIELDS = UnitSearchField.allowedFields();

    private final UnitRepository unitRepository;

    public UnitServiceImpl(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Transactional
    @Override
    public SuccessResponse create(CreateUnitRequest request,
                                  UnitTypeEntity unitTypeEntity,
                                  Map<Long, LocaleEntity> localeEntityMap) {
        UnitEntity entity = UnitMapper.create(request);
        unitTypeEntity.addUnitEntity(entity);
        if (request.getLocales() != null) {
            request.getLocales().forEach(localeReq -> {
                LocaleEntity localeEntity = localeEntityMap.get(localeReq.getLocaleId());
                UnitLocaleEntity locale = UnitLocaleMapper.create(localeReq, localeEntity);
                entity.addUnitLocaleEntity(locale);
            });
        }
        unitRepository.save(entity);
        log.info("Unit created with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public UnitResponse getById(Long id) {
        UnitEntity entity = getEntityById(id);
        UnitDto dto = UnitMapper.toDto(entity);
        return new UnitResponse(dto);
    }

    @Override
    public PaginatedResponse<UnitDto> getAll(UnitFilterRequest request, Long unitTypeId) {
        Page<@NonNull UnitDto> page = unitRepository
                .findAll(UnitSpecification.filter(request, unitTypeId), request.toPageable(ALLOWED_SORT_FIELDS))
                .map(UnitMapper::toDto);
        return Pagination.buildPaginatedResponse(page, ALLOWED_SORT_FIELDS, ALLOWED_SEARCH_FIELDS);
    }

    @Transactional
    @Override
    public SuccessResponse update(UnitEntity entity, UpdateUnitRequest request) {
        UnitMapper.update(entity, request);
        unitRepository.save(entity);
        log.info("Unit updated with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional
    @Override
    public SuccessResponse delete(Long id) {
        UnitEntity entity = getEntityById(id);
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        unitRepository.save(entity);
        log.info("Unit soft-deleted with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public UnitEntity getEntityById(Long id) {
        return unitRepository.findByIdAndIsActiveAndIsDeleted(id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("Unit not found with id: " + id));
    }

    @Override
    public List<UnitEntity> getAll(Set<Long> ids) {
        List<UnitEntity> entities = unitRepository.findAllByIdInAndIsActiveAndIsDeleted(ids, true, false);
        EntityValidator.validateAllFound(ids, entities, UnitEntity::getId, "Unit");
        return entities;
    }
}
