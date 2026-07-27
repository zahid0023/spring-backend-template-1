package com.example.springbackendtemplate1.address.model.mapper;

import com.example.springbackendtemplate1.address.dto.request.country.CountryRequest;
import com.example.springbackendtemplate1.address.dto.request.country.CreateCountryRequest;
import com.example.springbackendtemplate1.address.dto.request.country.UpdateCountryRequest;
import com.example.springbackendtemplate1.address.model.dto.CountryDto;
import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.currency.model.mapper.CurrencyMapper;
import lombok.experimental.UtilityClass;

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
                        .map(CountryLocaleMapper::toDto)
                        .toList())
                .cities(entity.getCityEntities().stream()
                        .map(city -> CityMapper.toDto(city, false))
                        .toList())
                .currencies(entity.getCurrencyEntities().stream()
                        .map(CurrencyMapper::toDto)
                        .toList())
                .build();
    }
}
