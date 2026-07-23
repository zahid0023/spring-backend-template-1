package com.example.springbackendtemplate1.unit.model.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum UnitSortField {
    ID("id"),
    CODE("code"),
    SYMBOL("symbol"),
    SORT_ORDER("sortOrder"),
    CREATED_AT("createdAt");

    private final String fieldName;

    UnitSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values())
                .map(UnitSortField::getFieldName)
                .collect(Collectors.toSet());
    }
}
