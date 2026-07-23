package com.example.springbackendtemplate1.unit.dto.response.unittypes;

import com.example.springbackendtemplate1.unit.model.dto.UnitTypeLocaleDto;
import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UnitTypeLocaleResponse {
    private final UnitTypeLocaleDto unitTypeLocale;

    public UnitTypeLocaleResponse(UnitTypeLocaleDto unitTypeLocale) {
        this.unitTypeLocale = unitTypeLocale;
    }
}
