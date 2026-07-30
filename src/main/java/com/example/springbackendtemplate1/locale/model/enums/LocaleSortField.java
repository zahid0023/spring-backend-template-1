package com.example.springbackendtemplate1.locale.model.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum LocaleSortField {

    ID("id", false),
    CREATED_AT("createdAt", false),
    SORT_ORDER("sortOrder", false),
    CODE("code", false),
    NAME("name", false);

    private final String fieldName;
    private final boolean localeField;

    LocaleSortField(String fieldName, boolean localeField) {
        this.fieldName = fieldName;
        this.localeField = localeField;
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean isLocaleField() {
        return localeField;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values())
                .map(LocaleSortField::getFieldName)
                .collect(Collectors.toSet());
    }

    public static Set<String> localeSortFields() {
        return Arrays.stream(values())
                .filter(LocaleSortField::isLocaleField)
                .map(LocaleSortField::getFieldName)
                .collect(Collectors.toSet());
    }
}
