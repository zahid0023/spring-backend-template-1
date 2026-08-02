package com.example.springbackendtemplate1.image.hosting.model.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum ImageHostingProviderSortField {
    CODE("code"),
    NAME("name");

    private final String fieldName;

    ImageHostingProviderSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values())
                .map(ImageHostingProviderSortField::getFieldName)
                .collect(Collectors.toSet());
    }
}
