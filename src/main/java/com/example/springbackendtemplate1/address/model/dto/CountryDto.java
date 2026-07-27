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
public class CountryDto {

    private Long id;
    private String code;
    private String iso3Code;
    private String phoneCode;
    private Integer sortOrder;

    @Builder.Default
    private List<CountryLocaleDto> locales = new ArrayList<>();

    @Builder.Default
    private List<CityDto> cities = new ArrayList<>();
}
