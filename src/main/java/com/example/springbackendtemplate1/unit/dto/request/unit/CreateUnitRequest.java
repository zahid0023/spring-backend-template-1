package com.example.springbackendtemplate1.unit.dto.request.unit;

import com.example.springbackendtemplate1.unit.dto.request.unit.locale.UnitLocaleRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateUnitRequest extends UnitRequest {

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotNull
    private Long unitTypeId;

    @Valid
    @NotNull
    private UnitLocaleRequest locale;
}
