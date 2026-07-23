package com.example.springbackendtemplate1.currency.service;

import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.currency.dto.request.currency.CreateCurrencyRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.CurrencyFilterRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.UpdateCurrencyRequest;
import com.example.springbackendtemplate1.currency.dto.response.currencies.CurrencyResponse;
import com.example.springbackendtemplate1.currency.model.dto.CurrencyDto;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CurrencyService {

    SuccessResponse create(CreateCurrencyRequest request,
                           CountryEntity countryEntity,
                           Map<Long, LocaleEntity> localeEntityMap);

    CurrencyEntity getEntityById(Long id);

    CurrencyResponse getById(Long id);

    PaginatedResponse<CurrencyDto> getAll(CurrencyFilterRequest request);

    SuccessResponse update(CurrencyEntity entity, UpdateCurrencyRequest request);

    SuccessResponse delete(Long id);

    List<CurrencyEntity> getAll(Set<Long> ids);
}
