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
    @Size(max = 10)
    private String iso3Code;

    @NotBlank
    @Size(max = 10)
    @Pattern(regexp = "^[A-Za-z]{1,3}$")
    private String phoneCode;

    @NotNull
    private Integer sortOrder;

}
