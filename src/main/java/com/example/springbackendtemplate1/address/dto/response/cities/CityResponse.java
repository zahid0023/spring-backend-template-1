package com.example.springbackendtemplate1.address.dto.response.cities;

import com.example.springbackendtemplate1.address.model.dto.CityDto;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CityResponse {
    private final CityDto city;

    public CityResponse(CityDto city) {
        this.city = city;
    }
}
