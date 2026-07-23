package com.example.springbackendtemplate1.unit.model.dto;

import com.example.springbackendtemplate1.locale.model.dto.LocaleDto;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UnitLocaleDto {
    private Long id;
    private LocaleDto locale;
    private String name;
    private String pluralName;
    private String description;
    private Integer sortOrder;
}
