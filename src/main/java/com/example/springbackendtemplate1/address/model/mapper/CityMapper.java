package com.example.springbackendtemplate1.address.model.mapper;

import com.example.springbackendtemplate1.address.dto.request.city.CityRequest;
import com.example.springbackendtemplate1.address.dto.request.city.CreateCityRequest;
import com.example.springbackendtemplate1.address.dto.request.city.UpdateCityRequest;
import com.example.springbackendtemplate1.address.model.dto.CityDto;
import com.example.springbackendtemplate1.address.model.dto.CityLocaleDto;
import com.example.springbackendtemplate1.address.model.entity.CityEntity;
import com.example.springbackendtemplate1.address.model.entity.CityLocaleEntity;
import com.example.springbackendtemplate1.commons.context.LocaleContext;
import lombok.experimental.UtilityClass;

import java.util.List;

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

    public CityDto.CityDtoBuilder toDto(CityEntity entity) {
        return CityDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .sortOrder(entity.getSortOrder())
                .locale(singleLocale(entity));
    }

    private CityLocaleDto singleLocale(CityEntity entity) {
        CityLocaleEntity matched = matchLocale(entity, LocaleContext.getLocaleId());
        return matched == null ? null : CityLocaleMapper.toDto(matched);
    }

    private List<CityLocaleEntity> activeLocales(CityEntity entity) {
        return entity.getCityLocaleEntities().stream()
                .filter(cityLocaleEntity -> Boolean.TRUE.equals(cityLocaleEntity.getIsActive())
                        && Boolean.FALSE.equals(cityLocaleEntity.getIsDeleted()))
                .toList();
    }

    private CityLocaleEntity matchLocale(CityEntity entity, Long localeId) {
        List<CityLocaleEntity> activeLocales = activeLocales(entity);
        return activeLocales.stream()
                .filter(cityLocaleEntity -> cityLocaleEntity.getLocaleEntity().getId().equals(localeId))
                .findFirst()
                .orElseGet(() -> activeLocales.stream()
                        .filter(cityLocaleEntity -> "en".equals(cityLocaleEntity.getLocaleEntity().getCode()))
                        .findFirst()
                        .orElse(null));
    }
}
