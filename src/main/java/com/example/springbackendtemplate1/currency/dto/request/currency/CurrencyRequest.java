package com.example.springbackendtemplate1.currency.dto.request.currency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CurrencyRequest {

    @NotBlank
    @Size(max = 3)
    private String code;

    @NotBlank
    @Size(max = 10)
    private String symbol;

    @NotNull
    private Integer decimalPlaces;

    @NotNull
    private Boolean isDefault;

    @NotNull
    private Integer sortOrder;

    @Size(max = 3)
    private String numericCode;
}
