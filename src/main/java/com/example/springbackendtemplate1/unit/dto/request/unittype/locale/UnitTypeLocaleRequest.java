package com.example.springbackendtemplate1.unit.dto.request.unittype.locale;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UnitTypeLocaleRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private String description;

    @NotNull
    private Integer sortOrder;

}
