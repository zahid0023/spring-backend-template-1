package com.example.springbackendtemplate1.unit.dto.request.unit;

import com.example.springbackendtemplate1.unit.dto.request.unit.unitlocale.CreateUnitLocaleRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateUnitRequest extends UnitRequest {

    @NotNull
    private Long unitTypeId;

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotBlank
    @Size(max = 20)
    private String symbol;

    private Boolean isBaseUnit;

    private BigDecimal conversionFactor;

    @Valid
    private List<CreateUnitLocaleRequest> locales;
}
