package com.example.springbackendtemplate1.unit.dto.request.unittype;

import com.example.springbackendtemplate1.unit.dto.request.unittype.locale.UnitTypeLocaleRequest;
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
public class CreateUnitTypeRequest extends UnitTypeRequest {

    @NotBlank
    @Size(max = 50)
    private String code;

    @Valid
    @NotNull
    private UnitTypeLocaleRequest locale;

}
