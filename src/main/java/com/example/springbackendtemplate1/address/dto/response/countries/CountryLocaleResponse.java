package com.example.springbackendtemplate1.address.dto.response.countries;

import com.example.springbackendtemplate1.address.model.dto.CountryLocaleDto;
import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CountryLocaleResponse {
    private final CountryLocaleDto countryLocale;

    public CountryLocaleResponse(CountryLocaleDto countryLocale) {
        this.countryLocale = countryLocale;
    }
}
