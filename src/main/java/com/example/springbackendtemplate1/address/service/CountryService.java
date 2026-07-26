package com.example.springbackendtemplate1.address.service;

import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.address.dto.request.country.CountryFilterRequest;
import com.example.springbackendtemplate1.address.dto.request.country.CreateCountryRequest;
import com.example.springbackendtemplate1.address.dto.request.country.UpdateCountryRequest;
import com.example.springbackendtemplate1.address.dto.response.countries.CountryResponse;
import com.example.springbackendtemplate1.address.model.dto.CountryDto;
import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;

import java.util.List;
import java.util.Set;

public interface CountryService {

    SuccessResponse create(CreateCountryRequest request,
                           List<LocaleEntity> localeEntities);

    CountryEntity getEntityById(Long id);

    CountryResponse getById(Long id);

    PaginatedResponse<CountryDto> getAll(CountryFilterRequest request);

    SuccessResponse update(CountryEntity entity,
                           UpdateCountryRequest request);

    SuccessResponse delete(CountryEntity entity);

    List<CountryEntity> getAll(Set<Long> ids);
}
