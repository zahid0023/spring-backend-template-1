package com.example.springbackendtemplate1.unit.model.enums;

import com.example.springbackendtemplate1.unit.dto.request.unittype.UnitTypeFilterRequest;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum UnitTypeSearchField {
    CODE("code", UnitTypeFilterRequest::getCode);

    private final String fieldName;
    private final Function<UnitTypeFilterRequest, String> valueExtractor;

    UnitTypeSearchField(String fieldName, Function<UnitTypeFilterRequest, String> valueExtractor) {
        this.fieldName = fieldName;
        this.valueExtractor = valueExtractor;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values())
                .map(UnitTypeSearchField::getFieldName)
                .collect(Collectors.toSet());
    }
}
