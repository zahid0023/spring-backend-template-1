package com.example.springbackendtemplate1.address.dto.request.country;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CountryRequest {

    @NotBlank
    @Size(max = 3)
    @Pattern(regexp = "^[A-Z]{3}$")
    private String iso3Code;

    @NotBlank
    @Size(max = 3)
    @Pattern(regexp = "^[0-9]{1,3}$")
    private String phoneCode;

    @NotNull
    private Integer sortOrder;

}
