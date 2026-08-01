package com.example.springbackendtemplate1.unit.serviceImpl;

import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.unit.dto.request.unit.locale.CreateUnitLocaleRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.locale.UpdateUnitLocaleRequest;
import com.example.springbackendtemplate1.unit.model.entity.UnitEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.unit.model.mapper.UnitLocaleMapper;
import com.example.springbackendtemplate1.unit.repository.UnitLocaleRepository;
import com.example.springbackendtemplate1.unit.service.UnitLocaleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UnitLocaleServiceImpl implements UnitLocaleService {
    private final UnitLocaleRepository unitLocaleRepository;

    public UnitLocaleServiceImpl(UnitLocaleRepository unitLocaleRepository) {
        this.unitLocaleRepository = unitLocaleRepository;
    }

    @Transactional
    @Override
    public SuccessResponse create(CreateUnitLocaleRequest request,
                                  UnitEntity unitEntity,
                                  LocaleEntity localeEntity) {
        if (unitLocaleRepository.existsByUnitEntity_IdAndLocaleEntity_IdAndIsActiveAndIsDeleted(
                unitEntity.getId(), localeEntity.getId(), true, false)) {
            throw new IllegalStateException("Unit already has a locale entry for locale id: " + localeEntity.getId());
        }

        UnitLocaleEntity entity = UnitLocaleMapper.create(request);
        unitEntity.addUnitLocaleEntity(entity);
        localeEntity.addUnitLocaleEntity(entity);
        unitLocaleRepository.save(entity);
        log.info("UnitLocale created with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional
    @Override
    public SuccessResponse update(UnitLocaleEntity entity,
                                  UpdateUnitLocaleRequest request) {
        UnitLocaleMapper.update(entity, request);
        unitLocaleRepository.save(entity);
        log.info("UnitLocale updated with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional
    @Override
    public SuccessResponse delete(UnitLocaleEntity entity) {
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        unitLocaleRepository.save(entity);
        log.info("UnitLocale soft-deleted with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public UnitLocaleEntity getEntityById(Long unitId, Long id) {
        return unitLocaleRepository
                .findByUnitEntity_IdAndIdAndIsActiveAndIsDeleted(unitId, id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("UnitLocale not found with id: " + id));
    }
}
