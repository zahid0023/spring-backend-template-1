package com.example.springbackendtemplate1.locale.model.enums;

import com.example.springbackendtemplate1.commons.utils.SearchType;
import com.example.springbackendtemplate1.locale.dto.request.locale.LocaleFilterRequest;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum LocaleSearchField {
    CODE("code", LocaleFilterRequest::getCode, SearchType.LIKE),
    NAME("name", LocaleFilterRequest::getName, SearchType.LIKE);

    private final String fieldName;
    private final Function<LocaleFilterRequest, String> valueExtractor;
    private final SearchType searchType;

    LocaleSearchField(String fieldName, Function<LocaleFilterRequest, String> valueExtractor, SearchType searchType) {
        this.fieldName = fieldName;
        this.valueExtractor = valueExtractor;
        this.searchType = searchType;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values())
                .map(LocaleSearchField::getFieldName)
                .collect(Collectors.toSet());
    }
}
