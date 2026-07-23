package com.example.springbackendtemplate1.locale.model.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum LocaleSortField {
    ID("id"),
    CODE("code"),
    NAME("name"),
    SORT_ORDER("sortOrder"),
    CREATED_AT("createdAt");

    private final String fieldName;

    LocaleSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values())
                .map(LocaleSortField::getFieldName)
                .collect(Collectors.toSet());
    }
}
