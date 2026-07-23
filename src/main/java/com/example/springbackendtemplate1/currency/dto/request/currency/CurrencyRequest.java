package com.example.springbackendtemplate1.currency.dto.request.currency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CurrencyRequest {

    @Size(max = 3)
    private String numericCode;

    @NotBlank
    @Size(max = 10)
    private String symbol;

    @NotNull
    private Integer decimalPlaces;

    @NotNull
    private Boolean isDefault;

    @NotNull
    private Integer sortOrder;
}
