package com.example.springbackendtemplate1.unit.dto.response.unittypes;

import com.example.springbackendtemplate1.unit.model.dto.UnitTypeDto;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UnitTypeResponse {
    private final UnitTypeDto data;

    public UnitTypeResponse(UnitTypeDto data) {
        this.data = data;
    }
}
