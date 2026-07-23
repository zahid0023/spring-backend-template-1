package com.example.springbackendtemplate1.address.dto.request.country;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CountryRequest {
    @Size(max = 10)
    private String iso3Code;

    @Size(max = 10)
    private String phoneCode;

    @NotNull
    private Integer sortOrder;
}
