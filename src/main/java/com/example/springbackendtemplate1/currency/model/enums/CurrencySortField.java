package com.example.springbackendtemplate1.currency.model.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum CurrencySortField {
    ID("id"),
    CODE("code"),
    SORT_ORDER("sortOrder"),
    CREATED_AT("createdAt");

    private final String fieldName;

    CurrencySortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values())
                .map(CurrencySortField::getFieldName)
                .collect(Collectors.toSet());
    }
}
