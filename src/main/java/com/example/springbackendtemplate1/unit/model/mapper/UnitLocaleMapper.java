package com.example.springbackendtemplate1.unit.model.mapper;

import com.example.springbackendtemplate1.unit.dto.request.unit.unitlocale.CreateUnitLocaleRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.unitlocale.UnitLocaleRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.unitlocale.UpdateUnitLocaleRequest;
import com.example.springbackendtemplate1.unit.model.dto.UnitLocaleDto;
import com.example.springbackendtemplate1.unit.model.entity.UnitEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.locale.model.mapper.LocaleMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UnitLocaleMapper {

    public UnitLocaleEntity create(CreateUnitLocaleRequest request,
                                   UnitEntity unitEntity,
                                   LocaleEntity localeEntity) {
        UnitLocaleEntity entity = new UnitLocaleEntity();
        entity.setUnitEntity(unitEntity);
        entity.setLocaleEntity(localeEntity);
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
