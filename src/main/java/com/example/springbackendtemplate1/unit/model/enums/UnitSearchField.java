package com.example.springbackendtemplate1.unit.model.enums;

import com.example.springbackendtemplate1.unit.dto.request.unit.UnitFilterRequest;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum UnitSearchField {
    CODE("code", UnitFilterRequest::getCode),
    SYMBOL("symbol", UnitFilterRequest::getSymbol);

    private final String fieldName;
    private final Function<UnitFilterRequest, String> valueExtractor;

    UnitSearchField(String fieldName, Function<UnitFilterRequest, String> valueExtractor) {
        this.fieldName = fieldName;
        this.valueExtractor = valueExtractor;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values())
                .map(UnitSearchField::getFieldName)
                .collect(Collectors.toSet());
    }
}
