package com.example.springbackendtemplate1.currency.service;

import com.example.springbackendtemplate1.commons.dto.request.PaginatedRequest;
import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.currency.dto.request.currency.locale.CreateCurrencyLocaleRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.locale.UpdateCurrencyLocaleRequest;
import com.example.springbackendtemplate1.currency.model.dto.CurrencyLocaleDto;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyEntity;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;

public interface CurrencyLocaleService {
    SuccessResponse create(CreateCurrencyLocaleRequest request,
                           CurrencyEntity currencyEntity,
                           LocaleEntity localeEntity);

    CurrencyLocaleEntity getEntityById(Long currencyId, Long id);

    PaginatedResponse<CurrencyLocaleDto> getAll(Long currencyId, String localeCode, PaginatedRequest paginatedRequest);

    SuccessResponse update(CurrencyLocaleEntity entity,
                           UpdateCurrencyLocaleRequest request);

    SuccessResponse delete(CurrencyLocaleEntity entity);
}
