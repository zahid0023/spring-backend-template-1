package com.example.springbackendtemplate1.address.dto.response.cities;

import com.example.springbackendtemplate1.address.model.dto.CityLocaleDto;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CityLocaleResponse {
    private final CityLocaleDto cityLocale;

    public CityLocaleResponse(CityLocaleDto cityLocale) {
        this.cityLocale = cityLocale;
    }
}
