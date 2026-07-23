package com.example.springbackendtemplate1.unit.model.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum UnitTypeSortField {
    ID("id"),
    CODE("code"),
    SORT_ORDER("sortOrder"),
    CREATED_AT("createdAt");

    private final String fieldName;

    UnitTypeSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values())
                .map(UnitTypeSortField::getFieldName)
                .collect(Collectors.toSet());
    }
}
