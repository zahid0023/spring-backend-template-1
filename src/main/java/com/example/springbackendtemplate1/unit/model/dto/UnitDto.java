package com.example.springbackendtemplate1.unit.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UnitDto {

    private Long id;

    private UnitTypeDto unitType;

    private String code;

    private String symbol;

    private Boolean isBaseUnit;

    private BigDecimal conversionFactor;

    private Integer sortOrder;

    @Builder.Default
    private List<UnitLocaleDto> locales = new ArrayList<>();
}
