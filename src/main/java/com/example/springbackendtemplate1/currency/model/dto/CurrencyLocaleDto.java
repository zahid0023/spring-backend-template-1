package com.example.springbackendtemplate1.currency.model.dto;

import com.example.springbackendtemplate1.locale.model.dto.LocaleDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CurrencyLocaleDto {

    private Long id;

    private LocaleDto locale;

    private String name;

    private String shortName;

    private Integer sortOrder;
}
