package com.example.springbackendtemplate1.unit.dto.response.unittypes;

import com.example.springbackendtemplate1.unit.model.dto.UnitTypeDto;
import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UnitTypeResponse {
    private final UnitTypeDto unitType;

    public UnitTypeResponse(UnitTypeDto unitType) {
        this.unitType = unitType;
    }
}
