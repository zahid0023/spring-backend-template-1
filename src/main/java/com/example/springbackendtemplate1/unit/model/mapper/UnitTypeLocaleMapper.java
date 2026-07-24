package com.example.springbackendtemplate1.unit.model.mapper;

import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.locale.model.mapper.LocaleMapper;
import com.example.springbackendtemplate1.unit.dto.request.unittype.unittypelocale.CreateUnitTypeLocaleRequest;
import com.example.springbackendtemplate1.unit.dto.request.unittype.unittypelocale.UnitTypeLocaleRequest;
import com.example.springbackendtemplate1.unit.dto.request.unittype.unittypelocale.UpdateUnitTypeLocaleRequest;
import com.example.springbackendtemplate1.unit.model.dto.UnitTypeLocaleDto;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeLocaleEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UnitTypeLocaleMapper {

    public UnitTypeLocaleEntity create(CreateUnitTypeLocaleRequest request,
                                       LocaleEntity localeEntity) {
        UnitTypeLocaleEntity entity = new UnitTypeLocaleEntity();
        entity.assignLocaleEntity(localeEntity);
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
