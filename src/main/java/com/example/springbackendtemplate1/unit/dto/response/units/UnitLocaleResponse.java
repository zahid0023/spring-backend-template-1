package com.example.springbackendtemplate1.unit.dto.response.units;

import com.example.springbackendtemplate1.unit.model.dto.UnitLocaleDto;
import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UnitLocaleResponse {
    private final UnitLocaleDto unitLocale;

    public UnitLocaleResponse(UnitLocaleDto unitLocale) {
        this.unitLocale = unitLocale;
    }
}
