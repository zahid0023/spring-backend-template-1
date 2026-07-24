package com.example.springbackendtemplate1.unit.dto.request.unit;

import com.example.springbackendtemplate1.unit.dto.request.unit.unitlocale.CreateUnitLocaleRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

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

    @Valid
    private List<CreateUnitLocaleRequest> locales;
}
