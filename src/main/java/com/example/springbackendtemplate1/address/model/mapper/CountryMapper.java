package com.example.springbackendtemplate1.address.model.mapper;

import com.example.springbackendtemplate1.address.dto.request.country.CountryRequest;
import com.example.springbackendtemplate1.address.dto.request.country.CreateCountryRequest;
import com.example.springbackendtemplate1.address.dto.request.country.UpdateCountryRequest;
import com.example.springbackendtemplate1.address.model.dto.CountryDto;
import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.address.model.entity.CountryLocaleEntity;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class CountryMapper {

    public CountryEntity create(CreateCountryRequest request) {
        CountryEntity entity = new CountryEntity();
        entity.setCode(request.getCode());
        applyCommonFields(entity, request);
        return entity;
    }

    public void update(CountryEntity entity, UpdateCountryRequest request) {
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(CountryEntity entity, CountryRequest request) {
        entity.setIso3Code(request.getIso3Code());
        entity.setPhoneCode(request.getPhoneCode());
        entity.setSortOrder(request.getSortOrder());
    }

    public CountryDto toDto(CountryEntity entity) {
        return CountryDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .iso3Code(entity.getIso3Code())
                .phoneCode(entity.getPhoneCode())
                .sortOrder(entity.getSortOrder())
                .locales(entity.getCountryLocaleEntities().stream()
                        .filter(countryLocaleEntity -> Boolean.TRUE.equals(countryLocaleEntity.getIsActive())
                                && Boolean.FALSE.equals(countryLocaleEntity.getIsDeleted()))
                        .map(CountryLocaleMapper::toDto)
                        .toList())
                .build();
    }

    public CountryDto toDto(CountryEntity entity, Long localeId) {
        List<CountryLocaleEntity> activeLocales = entity.getCountryLocaleEntities().stream()
                .filter(countryLocaleEntity -> Boolean.TRUE.equals(countryLocaleEntity.getIsActive())
                        && Boolean.FALSE.equals(countryLocaleEntity.getIsDeleted()))
                .toList();

        CountryLocaleEntity matched = activeLocales.stream()
                .filter(countryLocaleEntity -> countryLocaleEntity.getLocaleEntity().getId().equals(localeId))
                .findFirst()
                .orElseGet(() -> activeLocales.stream()
                        .filter(countryLocaleEntity -> "en".equals(countryLocaleEntity.getLocaleEntity().getCode()))
                        .findFirst()
                        .orElse(null));

        return CountryDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .iso3Code(entity.getIso3Code())
                .phoneCode(entity.getPhoneCode())
                .sortOrder(entity.getSortOrder())
                .locales(matched == null ? List.of() : List.of(CountryLocaleMapper.toDto(matched)))
                .build();
    }
}
