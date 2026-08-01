package com.example.springbackendtemplate1.unit.model.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum UnitSortField {
    CREATED_AT("createdAt", false),
    CODE("code", false),
    UNIT_TYPE_ID("unitTypeEntity.id", false),
    IS_BASE_UNIT("isBaseUnit", false),
    NAME("name", true);

    private final String fieldName;
    private final boolean localeField;

    UnitSortField(String fieldName, boolean localeField) {
        this.fieldName = fieldName;
        this.localeField = localeField;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values())
                .map(UnitSortField::getFieldName)
                .collect(Collectors.toSet());
    }

    public static Set<String> localeSortFields() {
        return Arrays.stream(values())
                .filter(UnitSortField::isLocaleField)
                .map(UnitSortField::getFieldName)
                .collect(Collectors.toSet());
    }
}
