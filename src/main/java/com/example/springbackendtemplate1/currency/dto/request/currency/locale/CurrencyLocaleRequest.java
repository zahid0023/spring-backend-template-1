package com.example.springbackendtemplate1.currency.dto.request.currency.locale;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CurrencyLocaleRequest {

    @NotBlank
    @Size(max = 200)
    private String name;

    @Size(max = 100)
    private String shortName;

    @NotNull
    private Integer sortOrder;
}
