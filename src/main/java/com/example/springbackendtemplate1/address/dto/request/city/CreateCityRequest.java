package com.example.springbackendtemplate1.address.dto.request.city;

import com.example.springbackendtemplate1.address.dto.request.city.citylocale.CreateCityLocaleRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateCityRequest extends CityRequest {

    @NotNull
    private Long countryId;

    @Valid
    private List<CreateCityLocaleRequest> locales;

}
