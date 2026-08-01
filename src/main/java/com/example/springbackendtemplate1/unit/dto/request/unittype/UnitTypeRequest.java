package com.example.springbackendtemplate1.unit.dto.request.unittype;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UnitTypeRequest {

    @NotNull
    private Integer sortOrder;
}
