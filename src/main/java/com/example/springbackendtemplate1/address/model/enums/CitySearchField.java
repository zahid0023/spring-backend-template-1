package com.example.springbackendtemplate1.address.model.enums;

import com.example.springbackendtemplate1.address.dto.request.city.CityFilterRequest;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum CitySearchField {
    CODE("code", CityFilterRequest::getCode);

    private final String fieldName;
    private final Function<CityFilterRequest, String> valueExtractor;

    CitySearchField(String fieldName, Function<CityFilterRequest, String> valueExtractor) {
        this.fieldName = fieldName;
        this.valueExtractor = valueExtractor;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values())
                .map(CitySearchField::getFieldName)
                .collect(Collectors.toSet());
    }
}
