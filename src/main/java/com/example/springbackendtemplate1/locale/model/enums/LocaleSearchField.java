package com.example.springbackendtemplate1.locale.model.enums;

import com.example.springbackendtemplate1.locale.dto.request.locale.LocaleFilterRequest;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum LocaleSearchField {
    CODE("code", LocaleFilterRequest::getCode),
    NAME("name", LocaleFilterRequest::getName);

    private final String fieldName;
    private final Function<LocaleFilterRequest, String> valueExtractor;

    LocaleSearchField(String fieldName, Function<LocaleFilterRequest, String> valueExtractor) {
        this.fieldName = fieldName;
        this.valueExtractor = valueExtractor;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values())
                .map(LocaleSearchField::getFieldName)
                .collect(Collectors.toSet());
    }
}
