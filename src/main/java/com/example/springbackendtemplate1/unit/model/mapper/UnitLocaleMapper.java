package com.example.springbackendtemplate1.unit.model.mapper;

import com.example.springbackendtemplate1.unit.dto.request.unit.locale.UnitLocaleRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.locale.UpdateUnitLocaleRequest;
import com.example.springbackendtemplate1.unit.model.dto.UnitLocaleDto;
import com.example.springbackendtemplate1.unit.model.entity.UnitLocaleEntity;
import com.example.springbackendtemplate1.locale.model.mapper.LocaleMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UnitLocaleMapper {

    public UnitLocaleEntity create(UnitLocaleRequest request) {
        UnitLocaleEntity entity = new UnitLocaleEntity();
        applyCommonFields(entity, request);
        return entity;
    }

    public void update(UnitLocaleEntity entity, UpdateUnitLocaleRequest request) {
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(UnitLocaleEntity entity, UnitLocaleRequest request) {
        entity.setName(request.getName());
        entity.setPluralName(request.getPluralName());
        entity.setDescription(request.getDescription());
        entity.setSortOrder(request.getSortOrder());
    }

    public UnitLocaleDto toDto(UnitLocaleEntity entity) {
        return UnitLocaleDto.builder()
                .id(entity.getId())
                .locale(LocaleMapper.toDto(entity.getLocaleEntity()))
                .name(entity.getName())
                .pluralName(entity.getPluralName())
                .description(entity.getDescription())
                .sortOrder(entity.getSortOrder())
                .build();
    }
}
