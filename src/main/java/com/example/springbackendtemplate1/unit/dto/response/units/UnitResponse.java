package com.example.springbackendtemplate1.unit.dto.response.units;

import com.example.springbackendtemplate1.unit.model.dto.UnitDto;
import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UnitResponse {
    private final UnitDto unit;

    public UnitResponse(UnitDto unit) {
        this.unit = unit;
    }
}
