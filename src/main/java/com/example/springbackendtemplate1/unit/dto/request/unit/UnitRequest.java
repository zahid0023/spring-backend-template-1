package com.example.springbackendtemplate1.unit.dto.request.unit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UnitRequest {

    @NotBlank
    @Size(max = 20)
    private String symbol;

    @NotNull
    private Boolean isBaseUnit;

    @NotNull
    @Positive
    private BigDecimal conversionFactor;

}
