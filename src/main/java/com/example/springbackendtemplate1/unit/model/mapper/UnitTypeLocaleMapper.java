package com.example.springbackendtemplate1.unit.model.mapper;

import com.example.springbackendtemplate1.unit.dto.request.unittype.locale.UnitTypeLocaleRequest;
import com.example.springbackendtemplate1.unit.dto.request.unittype.locale.UpdateUnitTypeLocaleRequest;
import com.example.springbackendtemplate1.unit.model.dto.UnitTypeLocaleDto;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeLocaleEntity;
import com.example.springbackendtemplate1.locale.model.mapper.LocaleMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UnitTypeLocaleMapper {

    public UnitTypeLocaleEntity create(UnitTypeLocaleRequest request) {
        UnitTypeLocaleEntity entity = new UnitTypeLocaleEntity();
        applyCommonFields(entity, request);
        return entity;
    }

    public void update(UnitTypeLocaleEntity entity, UpdateUnitTypeLocaleRequest request) {
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(UnitTypeLocaleEntity entity, UnitTypeLocaleRequest request) {
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setSortOrder(request.getSortOrder());
    }

    public UnitTypeLocaleDto toDto(UnitTypeLocaleEntity entity) {
        return UnitTypeLocaleDto.builder()
                .id(entity.getId())
                .locale(LocaleMapper.toDto(entity.getLocaleEntity()))
                .name(entity.getName())
                .description(entity.getDescription())
                .sortOrder(entity.getSortOrder())
                .build();
    }
}
