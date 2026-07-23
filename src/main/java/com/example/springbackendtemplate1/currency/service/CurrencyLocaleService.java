package com.example.springbackendtemplate1.currency.service;

import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.currency.dto.request.currency.currencylocale.CreateCurrencyLocaleRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.currencylocale.UpdateCurrencyLocaleRequest;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyEntity;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;

public interface CurrencyLocaleService {

    SuccessResponse create(CurrencyEntity currencyEntity,
                           LocaleEntity localeEntity,
                           CreateCurrencyLocaleRequest request);

    CurrencyLocaleEntity getEntityById(Long currencyId, Long id);

    SuccessResponse update(CurrencyLocaleEntity entity, UpdateCurrencyLocaleRequest request);

    SuccessResponse delete(CurrencyLocaleEntity entity);
}
