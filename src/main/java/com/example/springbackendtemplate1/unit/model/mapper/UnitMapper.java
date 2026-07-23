package com.example.springbackendtemplate1.unit.model.mapper;

import com.example.springbackendtemplate1.unit.dto.request.unit.CreateUnitRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.UnitRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.UpdateUnitRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.unitlocale.CreateUnitLocaleRequest;
import com.example.springbackendtemplate1.unit.model.dto.UnitDto;
import com.example.springbackendtemplate1.unit.model.dto.UnitLocaleDto;
import com.example.springbackendtemplate1.unit.model.entity.UnitEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeEntity;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class UnitMapper {

    public UnitEntity create(CreateUnitRequest request,
                             UnitTypeEntity unitTypeEntity,
                             Map<Long, LocaleEntity> localeEntityMap) {
        UnitEntity entity = new UnitEntity();
        entity.setUnitTypeEntity(unitTypeEntity);
        entity.setCode(request.getCode());
        entity.setSymbol(request.getSymbol());
        entity.setIsBaseUnit(request.getIsBaseUnit() != null ? request.getIsBaseUnit() : false);
        if (request.getConversionFactor() != null) {
            entity.setConversionFactor(request.getConversionFactor());
        }
        applyCommonFields(entity, request);
        entity.setUnitLocaleEntities(mapLocales(request.getLocales(), entity, localeEntityMap));
        return entity;
    }

    public void update(UnitEntity entity, UpdateUnitRequest request) {
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(UnitEntity entity, UnitRequest request) {
        entity.setSortOrder(request.getSortOrder());
    }

    private Set<UnitLocaleEntity> mapLocales(List<CreateUnitLocaleRequest> locales,
                                              UnitEntity entity,
                                              Map<Long, LocaleEntity> localeEntityMap) {
        if (locales == null) {
            return new java.util.LinkedHashSet<>();
        }
        return locales.stream()
                .map(locale -> UnitLocaleMapper.create(locale, entity, localeEntityMap.get(locale.getLocaleId())))
                .collect(Collectors.toSet());
    }

    public UnitDto toDto(UnitEntity entity) {
        List<UnitLocaleDto> locales = entity.getUnitLocaleEntities().stream()
                .map(UnitLocaleMapper::toDto)
                .toList();

        return UnitDto.builder()
                .id(entity.getId())
                .unitTypeId(entity.getUnitTypeEntity().getId())
                .code(entity.getCode())
                .symbol(entity.getSymbol())
                .isBaseUnit(entity.getIsBaseUnit())
                .conversionFactor(entity.getConversionFactor())
                .sortOrder(entity.getSortOrder())
                .locales(locales)
                .build();
    }
}
