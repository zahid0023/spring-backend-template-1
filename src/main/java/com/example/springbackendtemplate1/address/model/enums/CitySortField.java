package com.example.springbackendtemplate1.address.model.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum CitySortField {

    ID("id", false),
    CREATED_AT("createdAt", false),
    SORT_ORDER("sortOrder", false),
    CODE("code", false);

    private final String fieldName;
    private final boolean localeField;

    CitySortField(String fieldName, boolean localeField) {
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
        return Arrays.stream(values()).map(CitySortField::getFieldName).collect(Collectors.toSet());
    }

    public static Set<String> localeSortFields() {
        return Arrays.stream(values()).filter(CitySortField::isLocaleField)
                .map(CitySortField::getFieldName).collect(Collectors.toSet());
    }
}
