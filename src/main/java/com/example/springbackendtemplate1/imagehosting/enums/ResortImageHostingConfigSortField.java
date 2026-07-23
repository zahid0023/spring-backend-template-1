package com.example.springbackendtemplate1.imagehosting.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ResortImageHostingConfigSortField {
    ID("id"),
    PROVIDER("provider"),
    CREATED_AT("createdAt");

    private final String fieldName;

    ResortImageHostingConfigSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values())
                .map(ResortImageHostingConfigSortField::getFieldName)
                .collect(Collectors.toSet());
    }
}
