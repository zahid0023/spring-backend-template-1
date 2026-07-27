package com.example.springbackendtemplate1.address.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CityDto {
    private Long id;
    private CountryDto country;
    private String code;
    private Integer sortOrder;
    @Builder.Default
    private List<CityLocaleDto> locales = new ArrayList<>();
}
