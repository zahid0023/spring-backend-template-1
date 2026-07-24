package com.example.springbackendtemplate1.currency.model.mapper;

import com.example.springbackendtemplate1.currency.dto.request.currency.currencylocale.CreateCurrencyLocaleRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.currencylocale.CurrencyLocaleRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.currencylocale.UpdateCurrencyLocaleRequest;
import com.example.springbackendtemplate1.currency.model.dto.CurrencyLocaleDto;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CurrencyLocaleMapper {

    public CurrencyLocaleEntity create(CreateCurrencyLocaleRequest request,
                                       LocaleEntity localeEntity) {
        CurrencyLocaleEntity entity = new CurrencyLocaleEntity();
        entity.assignLocaleEntity(localeEntity);
        applyCommonFields(entity, request);
        return entity;
    }

    public void update(CurrencyLocaleEntity entity, UpdateCurrencyLocaleRequest request) {
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(CurrencyLocaleEntity entity, CurrencyLocaleRequest request) {
        entity.setName(request.getName());
        entity.setShortName(request.getShortName());
        entity.setSortOrder(request.getSortOrder());
    }

    public CurrencyLocaleDto toDto(CurrencyLocaleEntity entity) {
        return CurrencyLocaleDto.builder()
                .id(entity.getId())
                .localeId(entity.getLocaleEntity().getId())
                .name(entity.getName())
                .shortName(entity.getShortName())
                .sortOrder(entity.getSortOrder())
                .build();
    }
}
