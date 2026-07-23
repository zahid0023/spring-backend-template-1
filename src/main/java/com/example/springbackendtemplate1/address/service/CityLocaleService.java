package com.example.springbackendtemplate1.address.service;

import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.address.dto.request.city.citylocale.CreateCityLocaleRequest;
import com.example.springbackendtemplate1.address.dto.request.city.citylocale.UpdateCityLocaleRequest;
import com.example.springbackendtemplate1.address.model.entity.CityEntity;
import com.example.springbackendtemplate1.address.model.entity.CityLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;

public interface CityLocaleService {
    SuccessResponse create(CityEntity city,
                           LocaleEntity localeEntity,
                           CreateCityLocaleRequest request);

    CityLocaleEntity getEntityById(Long cityId, Long id);

    SuccessResponse update(CityLocaleEntity entity,
                           UpdateCityLocaleRequest request);

    SuccessResponse delete(CityLocaleEntity entity);
}
