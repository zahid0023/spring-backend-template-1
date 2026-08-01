package com.example.springbackendtemplate1.currency.model.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum CurrencySortField {

    CREATED_AT("createdAt", false),
    CODE("code", false),
    NUMERIC_CODE("numericCode", false),
    NAME("name", true),
    SHORT_NAME("shortName", true);

    private final String fieldName;
    private final boolean localeField;

    CurrencySortField(String fieldName, boolean localeField) {
        this.fieldName = fieldName;
        this.localeField = localeField;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values()).map(CurrencySortField::getFieldName).collect(Collectors.toSet());
    }

    public static Set<String> localeSortFields() {
        return Arrays.stream(values()).filter(CurrencySortField::isLocaleField)
                .map(CurrencySortField::getFieldName).collect(Collectors.toSet());
    }
}
