package com.example.springbackendtemplate1.address.dto.request.country.countrylocale;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateCountryLocaleRequest extends CountryLocaleRequest {

    @NotNull
    private Long localeId;
}
