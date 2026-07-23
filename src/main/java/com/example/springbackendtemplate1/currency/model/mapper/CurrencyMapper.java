package com.example.springbackendtemplate1.currency.model.mapper;

import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.currency.dto.request.currency.CreateCurrencyRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.CurrencyRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.UpdateCurrencyRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.currencylocale.CreateCurrencyLocaleRequest;
import com.example.springbackendtemplate1.currency.model.dto.CurrencyDto;
import com.example.springbackendtemplate1.currency.model.dto.CurrencyLocaleDto;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyEntity;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class CurrencyMapper {

    public CurrencyEntity create(CreateCurrencyRequest request,
                                 CountryEntity countryEntity,
                                 Map<Long, LocaleEntity> localeEntityMap) {
        CurrencyEntity entity = new CurrencyEntity();
        entity.setCode(request.getCode());
        entity.setCountryEntity(countryEntity);
        applyCommonFields(entity, request);
        entity.setCurrencyLocaleEntities(mapLocales(request.getLocales(), entity, localeEntityMap));
        return entity;
    }

    public void update(CurrencyEntity entity, UpdateCurrencyRequest request) {
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(CurrencyEntity entity, CurrencyRequest request) {
        entity.setNumericCode(request.getNumericCode());
        entity.setSymbol(request.getSymbol());
        entity.setDecimalPlaces(request.getDecimalPlaces());
        entity.setIsDefault(request.getIsDefault());
        entity.setSortOrder(request.getSortOrder());
    }

    private Set<CurrencyLocaleEntity> mapLocales(List<CreateCurrencyLocaleRequest> locales,
                                                  CurrencyEntity entity,
                                                  Map<Long, LocaleEntity> localeEntityMap) {
        if (locales == null || locales.isEmpty()) {
            return new java.util.LinkedHashSet<>();
        }
        return locales.stream()
                .map(locale -> CurrencyLocaleMapper.create(locale, entity, localeEntityMap.get(locale.getLocaleId())))
                .collect(Collectors.toSet());
    }

    public CurrencyDto toDto(CurrencyEntity entity) {
        List<CurrencyLocaleDto> locales = entity.getCurrencyLocaleEntities().stream()
                .filter(locale -> Boolean.TRUE.equals(locale.getIsActive()) && Boolean.FALSE.equals(locale.getIsDeleted()))
                .map(CurrencyLocaleMapper::toDto)
                .toList();

        return CurrencyDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .numericCode(entity.getNumericCode())
                .symbol(entity.getSymbol())
                .decimalPlaces(entity.getDecimalPlaces())
                .isDefault(entity.getIsDefault())
                .sortOrder(entity.getSortOrder())
                .countryId(entity.getCountryEntity().getId())
                .locales(locales)
                .build();
    }
}
