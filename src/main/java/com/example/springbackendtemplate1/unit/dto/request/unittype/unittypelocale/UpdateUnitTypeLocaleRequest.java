package com.example.springbackendtemplate1.unit.dto.request.unittype.unittypelocale;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateUnitTypeLocaleRequest extends UnitTypeLocaleRequest {
}
