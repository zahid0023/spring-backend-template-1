package com.example.springbackendtemplate1.unit.model.mapper;

import com.example.springbackendtemplate1.unit.dto.request.unit.CreateUnitRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.UnitRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.UpdateUnitRequest;
import com.example.springbackendtemplate1.unit.model.dto.UnitDto;
import com.example.springbackendtemplate1.unit.model.dto.UnitLocaleDto;
import com.example.springbackendtemplate1.unit.model.entity.UnitEntity;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class UnitMapper {

    public UnitEntity create(CreateUnitRequest request) {
        UnitEntity entity = new UnitEntity();
        entity.setCode(request.getCode());
        entity.setSymbol(request.getSymbol());
        entity.setIsBaseUnit(request.getIsBaseUnit() != null ? request.getIsBaseUnit() : false);
        if (request.getConversionFactor() != null) {
            entity.setConversionFactor(request.getConversionFactor());
        }
        applyCommonFields(entity, request);
        return entity;
    }

    public void update(UnitEntity entity, UpdateUnitRequest request) {
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(UnitEntity entity, UnitRequest request) {
        entity.setSortOrder(request.getSortOrder());
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
