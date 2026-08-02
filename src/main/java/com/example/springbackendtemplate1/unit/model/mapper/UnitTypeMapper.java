package com.example.springbackendtemplate1.unit.model.mapper;

import com.example.springbackendtemplate1.unit.dto.request.unittype.CreateUnitTypeRequest;
import com.example.springbackendtemplate1.unit.dto.request.unittype.UnitTypeRequest;
import com.example.springbackendtemplate1.unit.dto.request.unittype.UpdateUnitTypeRequest;
import com.example.springbackendtemplate1.unit.model.dto.UnitTypeDto;
import com.example.springbackendtemplate1.unit.model.dto.UnitTypeLocaleDto;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeLocaleEntity;
import com.example.springbackendtemplate1.commons.context.LocaleContext;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class UnitTypeMapper {

    public UnitTypeEntity create(CreateUnitTypeRequest request) {
        UnitTypeEntity entity = new UnitTypeEntity();
        entity.setCode(request.getCode());
        applyCommonFields(entity, request);
        return entity;
    }

    public void update(UnitTypeEntity entity, UpdateUnitTypeRequest request) {
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(UnitTypeEntity entity, UnitTypeRequest request) {
        entity.setSortOrder(request.getSortOrder());
    }

    public UnitTypeDto.UnitTypeDtoBuilder toDto(UnitTypeEntity entity) {
        return UnitTypeDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .sortOrder(entity.getSortOrder())
                .locale(singleLocale(entity));
    }

    private UnitTypeLocaleDto singleLocale(UnitTypeEntity entity) {
        UnitTypeLocaleEntity matched = matchLocale(entity, LocaleContext.getLocaleId());
        return matched == null ? null : UnitTypeLocaleMapper.toDto(matched);
    }

    private List<UnitTypeLocaleEntity> activeLocales(UnitTypeEntity entity) {
        return entity.getUnitTypeLocaleEntities().stream()
                .filter(unitTypeLocaleEntity -> Boolean.TRUE.equals(unitTypeLocaleEntity.getIsActive())
                        && Boolean.FALSE.equals(unitTypeLocaleEntity.getIsDeleted()))
                .toList();
    }

    private UnitTypeLocaleEntity matchLocale(UnitTypeEntity entity, Long localeId) {
        List<UnitTypeLocaleEntity> activeLocales = activeLocales(entity);
        return activeLocales.stream()
                .filter(unitTypeLocaleEntity -> unitTypeLocaleEntity.getLocaleEntity().getId().equals(localeId))
                .findFirst()
                .orElseGet(() -> activeLocales.stream()
                        .filter(unitTypeLocaleEntity -> "en".equals(unitTypeLocaleEntity.getLocaleEntity().getCode()))
                        .findFirst()
                        .orElse(null));
    }
}
