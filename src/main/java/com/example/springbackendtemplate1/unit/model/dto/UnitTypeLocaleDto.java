package com.example.springbackendtemplate1.unit.model.dto;

import com.example.springbackendtemplate1.locale.model.dto.LocaleDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UnitTypeLocaleDto {
    private Long id;
    private LocaleDto locale;
    private String name;
    private String description;
    private Integer sortOrder;
}
