package com.example.springbackendtemplate1.address.service;

import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.address.dto.request.country.countrylocale.CreateCountryLocaleRequest;
import com.example.springbackendtemplate1.address.dto.request.country.countrylocale.UpdateCountryLocaleRequest;
import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.address.model.entity.CountryLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;

public interface CountryLocaleService {
    SuccessResponse create(CountryEntity countryEntity,
                           LocaleEntity localeEntity,
                           CreateCountryLocaleRequest request);

    CountryLocaleEntity getEntityById(Long countryId, Long id);

    SuccessResponse update(CountryLocaleEntity entity,
                           UpdateCountryLocaleRequest request);

    SuccessResponse delete(CountryLocaleEntity entity);
}
