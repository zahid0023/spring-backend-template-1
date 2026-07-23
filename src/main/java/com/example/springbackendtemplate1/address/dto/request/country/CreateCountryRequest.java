package com.example.springbackendtemplate1.address.dto.request.country;

import com.example.springbackendtemplate1.address.dto.request.country.countrylocale.CreateCountryLocaleRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateCountryRequest extends CountryRequest {
    @NotBlank
    @Size(max = 10)
    private String code;

    private List<CreateCountryLocaleRequest> locales;
}
