package com.example.springbackendtemplate1.address.dto.request.city;

import com.example.springbackendtemplate1.address.dto.request.city.citylocale.CreateCityLocaleRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateCityRequest extends CityRequest {
    @NotNull
    private Long countryId;
    @Size(max = 50)
    private String code;
    private List<CreateCityLocaleRequest> locales;
}
