package com.example.springbackendtemplate1.unit.model.mapper;

import com.example.springbackendtemplate1.unit.dto.request.unittype.CreateUnitTypeRequest;
import com.example.springbackendtemplate1.unit.dto.request.unittype.UnitTypeRequest;
import com.example.springbackendtemplate1.unit.dto.request.unittype.UpdateUnitTypeRequest;
import com.example.springbackendtemplate1.unit.model.dto.UnitTypeDto;
import com.example.springbackendtemplate1.unit.model.dto.UnitTypeLocaleDto;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeEntity;
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

    public UnitTypeDto toDto(UnitTypeEntity entity) {
        List<UnitTypeLocaleDto> locales = entity.getUnitTypeLocaleEntities().stream()
                .map(UnitTypeLocaleMapper::toDto)
                .toList();

        return UnitTypeDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .sortOrder(entity.getSortOrder())
                .locales(locales)
                .build();
    }
}
