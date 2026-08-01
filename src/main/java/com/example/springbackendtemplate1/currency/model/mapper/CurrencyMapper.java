package com.example.springbackendtemplate1.currency.model.mapper;

import com.example.springbackendtemplate1.commons.context.LocaleContext;
import com.example.springbackendtemplate1.currency.dto.request.currency.CurrencyRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.CreateCurrencyRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.UpdateCurrencyRequest;
import com.example.springbackendtemplate1.currency.model.dto.CurrencyDto;
import com.example.springbackendtemplate1.currency.model.dto.CurrencyLocaleDto;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyEntity;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyLocaleEntity;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class CurrencyMapper {

    public CurrencyEntity create(CreateCurrencyRequest request) {
        CurrencyEntity entity = new CurrencyEntity();
        entity.setCode(request.getCode());
        entity.setNumericCode(request.getNumericCode());
        applyCommonFields(entity, request);
        return entity;
    }

    public void update(CurrencyEntity entity, UpdateCurrencyRequest request) {
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(CurrencyEntity entity, CurrencyRequest request) {
        entity.setSymbol(request.getSymbol());
        entity.setDecimalPlaces(request.getDecimalPlaces());
        entity.setIsDefault(request.getIsDefault());
        entity.setSortOrder(request.getSortOrder());
    }

    public CurrencyDto.CurrencyDtoBuilder toDto(CurrencyEntity entity, boolean includeLocales) {
        List<CurrencyLocaleDto> locales = includeLocales
                ? activeLocales(entity).stream()
                        .map(CurrencyLocaleMapper::toDto)
                        .toList()
                : singleLocale(entity);

        return CurrencyDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .numericCode(entity.getNumericCode())
                .symbol(entity.getSymbol())
                .decimalPlaces(entity.getDecimalPlaces())
                .isDefault(entity.getIsDefault())
                .sortOrder(entity.getSortOrder())
                .locales(locales);
    }

    private List<CurrencyLocaleDto> singleLocale(CurrencyEntity entity) {
        CurrencyLocaleEntity matched = matchLocale(entity, LocaleContext.getLocaleId());
        return matched == null ? List.of() : List.of(CurrencyLocaleMapper.toDto(matched));
    }

    private List<CurrencyLocaleEntity> activeLocales(CurrencyEntity entity) {
        return entity.getCurrencyLocaleEntities().stream()
                .filter(currencyLocaleEntity -> Boolean.TRUE.equals(currencyLocaleEntity.getIsActive())
                        && Boolean.FALSE.equals(currencyLocaleEntity.getIsDeleted()))
                .toList();
    }

    private CurrencyLocaleEntity matchLocale(CurrencyEntity entity, Long localeId) {
        List<CurrencyLocaleEntity> activeLocales = activeLocales(entity);
        return activeLocales.stream()
                .filter(currencyLocaleEntity -> currencyLocaleEntity.getLocaleEntity().getId().equals(localeId))
                .findFirst()
                .orElseGet(() -> activeLocales.stream()
                        .filter(currencyLocaleEntity -> "en".equals(currencyLocaleEntity.getLocaleEntity().getCode()))
                        .findFirst()
                        .orElse(null));
    }
}
