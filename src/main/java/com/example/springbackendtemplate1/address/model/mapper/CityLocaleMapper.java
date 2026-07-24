package com.example.springbackendtemplate1.address.model.mapper;

import com.example.springbackendtemplate1.address.dto.request.city.citylocale.CityLocaleRequest;
import com.example.springbackendtemplate1.address.dto.request.city.citylocale.CreateCityLocaleRequest;
import com.example.springbackendtemplate1.address.dto.request.city.citylocale.UpdateCityLocaleRequest;
import com.example.springbackendtemplate1.address.model.dto.CityLocaleDto;
import com.example.springbackendtemplate1.address.model.entity.CityLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CityLocaleMapper {

    public CityLocaleEntity create(CreateCityLocaleRequest request,
                                   LocaleEntity localeEntity) {
        CityLocaleEntity entity = new CityLocaleEntity();
        entity.setLocaleEntity(localeEntity);
        applyCommonFields(entity, request);
        return entity;
    }

    public void update(CityLocaleEntity entity, UpdateCityLocaleRequest request) {
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(CityLocaleEntity entity, CityLocaleRequest request) {
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setSortOrder(request.getSortOrder());
    }

    public CityLocaleDto toDto(CityLocaleEntity entity) {
        return CityLocaleDto.builder()
                .id(entity.getId())
                .localeId(entity.getLocaleEntity().getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .sortOrder(entity.getSortOrder())
                .build();
    }
}
