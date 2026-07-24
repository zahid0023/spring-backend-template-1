package com.example.springbackendtemplate1.locale.model.dto;

import com.example.springbackendtemplate1.address.model.dto.CityLocaleDto;
import com.example.springbackendtemplate1.address.model.dto.CountryLocaleDto;
import com.example.springbackendtemplate1.currency.model.dto.CurrencyLocaleDto;
import com.example.springbackendtemplate1.unit.model.dto.UnitLocaleDto;
import com.example.springbackendtemplate1.unit.model.dto.UnitTypeLocaleDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocaleDto {

    private Long id;
    private String code;
    private String name;
    private Integer sortOrder;

    @Builder.Default
    private List<CountryLocaleDto> countryLocales = new ArrayList<>();

    @Builder.Default
    private List<CityLocaleDto> cityLocales = new ArrayList<>();

    @Builder.Default
    private List<CurrencyLocaleDto> currencyLocales = new ArrayList<>();

    @Builder.Default
    private List<UnitTypeLocaleDto> unitTypeLocales = new ArrayList<>();

    @Builder.Default
    private List<UnitLocaleDto> unitLocales = new ArrayList<>();

}
