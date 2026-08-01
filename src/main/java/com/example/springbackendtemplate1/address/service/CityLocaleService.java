package com.example.springbackendtemplate1.address.service;

import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.address.dto.request.city.locale.CreateCityLocaleRequest;
import com.example.springbackendtemplate1.address.dto.request.city.locale.UpdateCityLocaleRequest;
import com.example.springbackendtemplate1.address.model.entity.CityEntity;
import com.example.springbackendtemplate1.address.model.entity.CityLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;

public interface CityLocaleService {
    SuccessResponse create(CreateCityLocaleRequest request,
                           CityEntity cityEntity,
                           LocaleEntity localeEntity);

    CityLocaleEntity getEntityById(Long cityId, Long id);

    SuccessResponse update(CityLocaleEntity entity,
                           UpdateCityLocaleRequest request);

    SuccessResponse delete(CityLocaleEntity entity);
}
