package com.example.springbackendtemplate1.unit.dto.request.unit.locale;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateUnitLocaleRequest extends UnitLocaleRequest {

    @NotNull
    private Long localeId;

}
