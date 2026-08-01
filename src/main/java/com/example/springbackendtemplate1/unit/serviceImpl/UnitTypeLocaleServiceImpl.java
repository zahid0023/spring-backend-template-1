package com.example.springbackendtemplate1.unit.serviceImpl;

import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.unit.dto.request.unittype.locale.CreateUnitTypeLocaleRequest;
import com.example.springbackendtemplate1.unit.dto.request.unittype.locale.UpdateUnitTypeLocaleRequest;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.unit.model.mapper.UnitTypeLocaleMapper;
import com.example.springbackendtemplate1.unit.repository.UnitTypeLocaleRepository;
import com.example.springbackendtemplate1.unit.service.UnitTypeLocaleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UnitTypeLocaleServiceImpl implements UnitTypeLocaleService {
    private final UnitTypeLocaleRepository unitTypeLocaleRepository;

    public UnitTypeLocaleServiceImpl(UnitTypeLocaleRepository unitTypeLocaleRepository) {
        this.unitTypeLocaleRepository = unitTypeLocaleRepository;
    }

    @Transactional
    @Override
    public SuccessResponse create(CreateUnitTypeLocaleRequest request,
                                  UnitTypeEntity unitTypeEntity,
                                  LocaleEntity localeEntity) {
        if (unitTypeLocaleRepository.existsByUnitTypeEntity_IdAndLocaleEntity_IdAndIsActiveAndIsDeleted(
                unitTypeEntity.getId(), localeEntity.getId(), true, false)) {
            throw new IllegalStateException("UnitTypeLocale already exists for unitTypeId '"
                    + unitTypeEntity.getId() + "' and localeId '" + localeEntity.getId() + "'");
        }

        UnitTypeLocaleEntity entity = UnitTypeLocaleMapper.create(request);
        unitTypeEntity.addUnitTypeLocaleEntity(entity);
        localeEntity.addUnitTypeLocaleEntity(entity);
        unitTypeLocaleRepository.save(entity);
        log.info("UnitTypeLocale created with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional
    @Override
    public SuccessResponse update(UnitTypeLocaleEntity entity,
                                  UpdateUnitTypeLocaleRequest request) {
        UnitTypeLocaleMapper.update(entity, request);
        unitTypeLocaleRepository.save(entity);
        log.info("UnitTypeLocale updated with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional
    @Override
    public SuccessResponse delete(UnitTypeLocaleEntity entity) {
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        unitTypeLocaleRepository.save(entity);
        log.info("UnitTypeLocale soft-deleted with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public UnitTypeLocaleEntity getEntityById(Long unitTypeId, Long id) {
        return unitTypeLocaleRepository
                .findByUnitTypeEntity_IdAndIdAndIsActiveAndIsDeleted(unitTypeId, id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("UnitTypeLocale not found with id: " + id));
    }
}
