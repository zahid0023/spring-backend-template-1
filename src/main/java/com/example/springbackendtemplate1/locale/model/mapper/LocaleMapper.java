package com.example.springbackendtemplate1.locale.model.mapper;

import com.example.springbackendtemplate1.locale.dto.request.locale.CreateLocaleRequest;
import com.example.springbackendtemplate1.locale.dto.request.locale.LocaleRequest;
import com.example.springbackendtemplate1.locale.dto.request.locale.UpdateLocaleRequest;
import com.example.springbackendtemplate1.locale.model.dto.LocaleDto;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LocaleMapper {

    public LocaleEntity create(CreateLocaleRequest request) {
        LocaleEntity entity = new LocaleEntity();
        applyCommonFields(entity, request);
        return entity;
    }

    public void update(LocaleEntity entity, UpdateLocaleRequest request) {
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(LocaleEntity entity, LocaleRequest request) {
        entity.setName(request.getName());
        entity.setSortOrder(request.getSortOrder());
    }

    public LocaleDto toDto(LocaleEntity entity) {
        return LocaleDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .sortOrder(entity.getSortOrder())
                .build();
    }
}
