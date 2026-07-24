package com.example.springbackendtemplate1.currency.model.mapper;

import com.example.springbackendtemplate1.currency.dto.request.currency.CreateCurrencyRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.CurrencyRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.UpdateCurrencyRequest;
import com.example.springbackendtemplate1.currency.model.dto.CurrencyDto;
import com.example.springbackendtemplate1.currency.model.dto.CurrencyLocaleDto;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyEntity;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class CurrencyMapper {

    public CurrencyEntity create(CreateCurrencyRequest request) {
        CurrencyEntity entity = new CurrencyEntity();
        entity.setCode(request.getCode());
        applyCommonFields(entity, request);
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
