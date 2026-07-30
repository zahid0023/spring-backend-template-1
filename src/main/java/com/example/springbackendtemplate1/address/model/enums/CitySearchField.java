package com.example.springbackendtemplate1.address.model.enums;

import com.example.springbackendtemplate1.address.dto.request.city.CityFilterRequest;
import com.example.springbackendtemplate1.commons.utils.SearchType;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum CitySearchField {
    CODE("code", SearchType.LIKE, false, null, CityFilterRequest::getCode);

    private final String fieldName;
    private final SearchType searchType;
    private final boolean localeField;
    private final String collectionField;
    private final Function<CityFilterRequest, String> valueExtractor;

    CitySearchField(String fieldName, SearchType searchType, boolean localeField,
                    String collectionField, Function<CityFilterRequest, String> valueExtractor) {
        this.fieldName = fieldName;
        this.searchType = searchType;
        this.localeField = localeField;
        this.collectionField = collectionField;
        this.valueExtractor = valueExtractor;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values()).map(CitySearchField::getFieldName).collect(Collectors.toSet());
    }

    public static Set<String> localeSearchFields() {
        return Arrays.stream(values()).filter(CitySearchField::isLocaleField)
                .map(CitySearchField::getFieldName).collect(Collectors.toSet());
    }
}
