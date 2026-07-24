package com.example.springbackendtemplate1.unit.dto.request.unit;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UnitRequest {

    @NotNull
    private Boolean isBaseUnit;

    @NotNull
    private BigDecimal conversionFactor;

    @NotNull
    private Integer sortOrder;
}
