package com.example.springbackendtemplate1.address.model.mapper;

import com.example.springbackendtemplate1.address.dto.request.country.countrylocale.CountryLocaleRequest;
import com.example.springbackendtemplate1.address.dto.request.country.countrylocale.CreateCountryLocaleRequest;
import com.example.springbackendtemplate1.address.dto.request.country.countrylocale.UpdateCountryLocaleRequest;
import com.example.springbackendtemplate1.address.model.dto.CountryLocaleDto;
import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.address.model.entity.CountryLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CountryLocaleMapper {

    public CountryLocaleEntity create(CreateCountryLocaleRequest request,
                                      CountryEntity countryEntity,
                                      LocaleEntity localeEntity) {
        CountryLocaleEntity entity = new CountryLocaleEntity();
        entity.setCountryEntity(countryEntity);
        entity.setLocaleEntity(localeEntity);
        applyCommonFields(entity, request);
        return entity;
    }

    public void update(CountryLocaleEntity entity, UpdateCountryLocaleRequest request) {
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(CountryLocaleEntity entity, CountryLocaleRequest request) {
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setSortOrder(request.getSortOrder());
    }

    public CountryLocaleDto toDto(CountryLocaleEntity entity) {
        return CountryLocaleDto.builder()
                .id(entity.getId())
                .localeId(entity.getLocaleEntity().getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .sortOrder(entity.getSortOrder())
                .build();
    }
}
