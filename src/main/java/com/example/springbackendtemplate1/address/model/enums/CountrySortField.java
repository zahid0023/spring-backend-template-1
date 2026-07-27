package com.example.springbackendtemplate1.address.model.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum CountrySortField {
    ID("id", false),
    CREATED_AT("createdAt", false),
    CODE("code", false),
    SORT_ORDER("sortOrder", false),
    NAME("name", true);

    private final String fieldName;
    private final boolean localeField;

    CountrySortField(String fieldName, boolean localeField) {
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
                .map(CountrySortField::getFieldName)
                .collect(Collectors.toSet());
    }

    public static Set<String> localeSortFields() {
        return Arrays.stream(values())
                .filter(CountrySortField::isLocaleField)
                .map(CountrySortField::getFieldName)
                .collect(Collectors.toSet());
    }
}
