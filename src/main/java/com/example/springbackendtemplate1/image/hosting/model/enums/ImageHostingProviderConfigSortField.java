package com.example.springbackendtemplate1.image.hosting.model.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum ImageHostingProviderConfigSortField {
    NAME("name"),
    IMAGE_HOSTING_PROVIDER_ID("imageHostingProviderEntity.id");

    private final String fieldName;

    ImageHostingProviderConfigSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values())
                .map(ImageHostingProviderConfigSortField::getFieldName)
                .collect(Collectors.toSet());
    }
}
