package com.example.springbackendtemplate1.address.model.mapper;

import com.example.springbackendtemplate1.address.dto.request.country.CountryRequest;
import com.example.springbackendtemplate1.address.dto.request.country.CreateCountryRequest;
import com.example.springbackendtemplate1.address.dto.request.country.UpdateCountryRequest;
import com.example.springbackendtemplate1.address.dto.request.country.countrylocale.CreateCountryLocaleRequest;
import com.example.springbackendtemplate1.address.model.dto.CityDto;
import com.example.springbackendtemplate1.address.model.dto.CountryDto;
import com.example.springbackendtemplate1.address.model.dto.CountryLocaleDto;
import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.address.model.entity.CountryLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@UtilityClass
public class CountryMapper {

    public CountryEntity create(CreateCountryRequest request,
                                Map<Long, LocaleEntity> localeEntityMap) {
        CountryEntity entity = new CountryEntity();
        entity.setCode(request.getCode());
        applyCommonFields(entity, request);
        entity.setCountryLocaleEntities(mapLocales(request.getLocales(), entity, localeEntityMap));
        return entity;
    }

    public void update(CountryEntity entity,
                       UpdateCountryRequest request) {
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(CountryEntity entity,
                                   CountryRequest request) {
        entity.setIso3Code(request.getIso3Code());
        entity.setPhoneCode(request.getPhoneCode());
        entity.setSortOrder(request.getSortOrder());
    }

    private Set<CountryLocaleEntity> mapLocales(List<CreateCountryLocaleRequest> locales,
                                                CountryEntity entity,
                                                Map<Long, LocaleEntity> localeEntityMap) {
        return locales.stream()
                .map(locale -> CountryLocaleMapper.create(locale, entity, localeEntityMap.get(locale.getLocaleId())))
                .collect(Collectors.toSet());
    }

    public CountryDto toDto(CountryEntity entity, Boolean includeCities) {
        List<CountryLocaleDto> countryLocales = entity.getCountryLocaleEntities().stream()
                .map(CountryLocaleMapper::toDto)
                .toList();

        List<CityDto> cities = includeCities ? entity.getCityEntities().stream()
                .map(city -> CityMapper.toDto(city, false))
                .toList() : List.of();

        return CountryDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .iso3Code(entity.getIso3Code())
                .phoneCode(entity.getPhoneCode())
                .sortOrder(entity.getSortOrder())
                .locales(countryLocales)
                .cities(cities)
                .build();
    }
}
