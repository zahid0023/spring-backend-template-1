package com.example.springbackendtemplate1.address.model.mapper;

import com.example.springbackendtemplate1.address.dto.request.city.CityRequest;
import com.example.springbackendtemplate1.address.dto.request.city.CreateCityRequest;
import com.example.springbackendtemplate1.address.dto.request.city.UpdateCityRequest;
import com.example.springbackendtemplate1.address.model.dto.CityDto;
import com.example.springbackendtemplate1.address.model.entity.CityEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CityMapper {

    public CityEntity create(CreateCityRequest request) {
        CityEntity entity = new CityEntity();
        entity.setCode(request.getCode());
        applyCommonFields(entity, request);
        return entity;
    }

    public void update(CityEntity entity, UpdateCityRequest request) {
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(CityEntity entity, CityRequest request) {
        entity.setSortOrder(request.getSortOrder());
    }

    public CityDto toDto(CityEntity entity) {
        return CityDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .sortOrder(entity.getSortOrder())
                .build();
    }
}
