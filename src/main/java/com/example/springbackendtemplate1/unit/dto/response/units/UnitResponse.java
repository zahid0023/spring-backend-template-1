package com.example.springbackendtemplate1.unit.dto.response.units;

import com.example.springbackendtemplate1.unit.model.dto.UnitDto;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UnitResponse {
    private final UnitDto data;

    public UnitResponse(UnitDto data) {
        this.data = data;
    }
}
