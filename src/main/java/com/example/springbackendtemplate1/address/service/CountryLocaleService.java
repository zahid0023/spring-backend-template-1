package com.example.springbackendtemplate1.address.service;

import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.address.dto.request.country.locale.CreateCountryLocaleRequest;
import com.example.springbackendtemplate1.address.dto.request.country.locale.UpdateCountryLocaleRequest;
import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.address.model.entity.CountryLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;

public interface CountryLocaleService {
    SuccessResponse create(CreateCountryLocaleRequest request,
                           CountryEntity countryEntity,
                           LocaleEntity localeEntity);

    CountryLocaleEntity getEntityById(Long countryId, Long id);

    SuccessResponse update(CountryLocaleEntity entity,
                           UpdateCountryLocaleRequest request);

    SuccessResponse delete(CountryLocaleEntity entity);
}
