package com.example.springbackendtemplate1.address.model.mapper;

import com.example.springbackendtemplate1.address.dto.request.country.CountryRequest;
import com.example.springbackendtemplate1.address.dto.request.country.CreateCountryRequest;
import com.example.springbackendtemplate1.address.dto.request.country.UpdateCountryRequest;
import com.example.springbackendtemplate1.address.model.dto.CountryDto;
import com.example.springbackendtemplate1.address.model.dto.CountryLocaleDto;
import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.address.model.entity.CountryLocaleEntity;
import com.example.springbackendtemplate1.commons.context.LocaleContext;
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

    public CountryDto.CountryDtoBuilder toDto(CountryEntity entity) {
        return CountryDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .iso3Code(entity.getIso3Code())
                .phoneCode(entity.getPhoneCode())
                .sortOrder(entity.getSortOrder())
                .locale(singleLocale(entity));
    }

    private List<CountryLocaleEntity> activeLocales(CountryEntity entity) {
        return entity.getCountryLocaleEntities().stream()
                .filter(countryLocaleEntity -> Boolean.TRUE.equals(countryLocaleEntity.getIsActive())
                        && Boolean.FALSE.equals(countryLocaleEntity.getIsDeleted()))
                .toList();
    }

    private CountryLocaleDto singleLocale(CountryEntity entity) {
        CountryLocaleEntity matched = matchLocale(entity, LocaleContext.getLocaleId());
        return matched == null ? null : CountryLocaleMapper.toDto(matched);
    }

    private CountryLocaleEntity matchLocale(CountryEntity entity, Long localeId) {
        List<CountryLocaleEntity> activeLocales = activeLocales(entity);
        return activeLocales.stream()
                .filter(countryLocaleEntity -> countryLocaleEntity.getLocaleEntity().getId().equals(localeId))
                .findFirst()
                .orElseGet(() -> activeLocales.stream()
                        .filter(countryLocaleEntity -> "en".equals(countryLocaleEntity.getLocaleEntity().getCode()))
                        .findFirst()
                        .orElse(null));
    }
}
