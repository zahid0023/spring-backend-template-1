package com.example.springbackendtemplate1.address.dto.response.countries;

import com.example.springbackendtemplate1.address.model.dto.CountryDto;
import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CountryResponse {
    private final CountryDto country;

    public CountryResponse(CountryDto country) {
        this.country = country;
    }
}
