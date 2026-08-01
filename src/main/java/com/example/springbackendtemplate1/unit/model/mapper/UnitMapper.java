package com.example.springbackendtemplate1.unit.model.mapper;

import com.example.springbackendtemplate1.unit.dto.request.unit.CreateUnitRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.UnitRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.UpdateUnitRequest;
import com.example.springbackendtemplate1.unit.model.dto.UnitDto;
import com.example.springbackendtemplate1.unit.model.dto.UnitLocaleDto;
import com.example.springbackendtemplate1.unit.model.entity.UnitEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitLocaleEntity;
import com.example.springbackendtemplate1.commons.context.LocaleContext;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class UnitMapper {

    public UnitEntity create(CreateUnitRequest request) {
        UnitEntity entity = new UnitEntity();
        entity.setCode(request.getCode());
        applyCommonFields(entity, request);
        return entity;
    }

    public void update(UnitEntity entity, UpdateUnitRequest request) {
        applyCommonFields(entity, request);
        entity.setSortOrder(request.getSortOrder());
    }

    private void applyCommonFields(UnitEntity entity, UnitRequest request) {
        entity.setSymbol(request.getSymbol());
        entity.setIsBaseUnit(request.getIsBaseUnit());
        entity.setConversionFactor(request.getConversionFactor());
    }

    public UnitDto.UnitDtoBuilder toDto(UnitEntity entity, boolean includeLocales) {
        List<UnitLocaleDto> locales = includeLocales
                ? activeLocales(entity).stream()
                        .map(UnitLocaleMapper::toDto)
                        .toList()
                : singleLocale(entity);

        return UnitDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .symbol(entity.getSymbol())
                .isBaseUnit(entity.getIsBaseUnit())
                .conversionFactor(entity.getConversionFactor())
                .sortOrder(entity.getSortOrder())
                .locales(locales);
    }

    private List<UnitLocaleDto> singleLocale(UnitEntity entity) {
        UnitLocaleEntity matched = matchLocale(entity, LocaleContext.getLocaleId());
        return matched == null ? List.of() : List.of(UnitLocaleMapper.toDto(matched));
    }

    private List<UnitLocaleEntity> activeLocales(UnitEntity entity) {
        return entity.getUnitLocaleEntities().stream()
                .filter(unitLocaleEntity -> Boolean.TRUE.equals(unitLocaleEntity.getIsActive())
                        && Boolean.FALSE.equals(unitLocaleEntity.getIsDeleted()))
                .toList();
    }

    private UnitLocaleEntity matchLocale(UnitEntity entity, Long localeId) {
        List<UnitLocaleEntity> activeLocales = activeLocales(entity);
        return activeLocales.stream()
                .filter(unitLocaleEntity -> unitLocaleEntity.getLocaleEntity().getId().equals(localeId))
                .findFirst()
                .orElseGet(() -> activeLocales.stream()
                        .filter(unitLocaleEntity -> "en".equals(unitLocaleEntity.getLocaleEntity().getCode()))
                        .findFirst()
                        .orElse(null));
    }
}
