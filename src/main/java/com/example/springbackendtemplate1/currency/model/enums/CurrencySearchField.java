package com.example.springbackendtemplate1.currency.model.enums;

import com.example.springbackendtemplate1.currency.dto.request.currency.CurrencyFilterRequest;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum CurrencySearchField {
    CODE("code", CurrencyFilterRequest::getCode),
    NUMERIC_CODE("numericCode", CurrencyFilterRequest::getNumericCode),
    SYMBOL("symbol", CurrencyFilterRequest::getSymbol);

    private final String fieldName;
    private final Function<CurrencyFilterRequest, String> valueExtractor;

    CurrencySearchField(String fieldName, Function<CurrencyFilterRequest, String> valueExtractor) {
        this.fieldName = fieldName;
        this.valueExtractor = valueExtractor;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values())
                .map(CurrencySearchField::getFieldName)
                .collect(Collectors.toSet());
    }
}
