package com.example.springbackendtemplate1.address.service;

import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.address.dto.request.city.CityFilterRequest;
import com.example.springbackendtemplate1.address.dto.request.city.CreateCityRequest;
import com.example.springbackendtemplate1.address.dto.request.city.UpdateCityRequest;
import com.example.springbackendtemplate1.address.dto.response.cities.CityResponse;
import com.example.springbackendtemplate1.address.model.dto.CityDto;
import com.example.springbackendtemplate1.address.model.entity.CityEntity;
import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;

public interface CityService {

    SuccessResponse create(CreateCityRequest request,
                           CountryEntity countryEntity,
                           LocaleEntity localeEntity);

    CityEntity getEntityById(Long id);

    CityResponse getById(Long id);

    PaginatedResponse<CityDto> getAll(CityFilterRequest request);

    SuccessResponse update(CityEntity entity,
                           UpdateCityRequest request);

    SuccessResponse delete(CityEntity entity);
}
