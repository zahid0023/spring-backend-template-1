package com.example.springbackendtemplate1.address.model.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum CitySortField {

    CREATED_AT("createdAt", false),
    SORT_ORDER("sortOrder", false),
    CODE("code", false),
    NAME("name", true);

    private final String fieldName;
    private final boolean localeField;

    CitySortField(String fieldName, boolean localeField) {
        this.fieldName = fieldName;
        this.localeField = localeField;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values()).map(CitySortField::getFieldName).collect(Collectors.toSet());
    }

    public static Set<String> localeSortFields() {
        return Arrays.stream(values()).filter(CitySortField::isLocaleField)
                .map(CitySortField::getFieldName).collect(Collectors.toSet());
    }
}
