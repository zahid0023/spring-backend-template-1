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
    CODE("code", SearchType.LIKE, false, null, LocaleFilterRequest::getCode),
    NAME("name", SearchType.LIKE, false, null, LocaleFilterRequest::getName);

    private final String fieldName;
    private final SearchType searchType;
    private final boolean localeField;
    private final String collectionField;
    private final Function<LocaleFilterRequest, String> valueExtractor;

    LocaleSearchField(String fieldName, SearchType searchType, boolean localeField,
                       String collectionField, Function<LocaleFilterRequest, String> valueExtractor) {
        this.fieldName = fieldName;
        this.searchType = searchType;
        this.localeField = localeField;
        this.collectionField = collectionField;
        this.valueExtractor = valueExtractor;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values()).map(LocaleSearchField::getFieldName).collect(Collectors.toSet());
    }

    public static Set<String> localeSearchFields() {
        return Arrays.stream(values()).filter(LocaleSearchField::isLocaleField)
                .map(LocaleSearchField::getFieldName).collect(Collectors.toSet());
    }
}
