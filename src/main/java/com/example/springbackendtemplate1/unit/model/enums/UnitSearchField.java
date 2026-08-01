package com.example.springbackendtemplate1.unit.model.enums;

import com.example.springbackendtemplate1.unit.dto.request.unit.UnitFilterRequest;
import com.example.springbackendtemplate1.commons.utils.SearchFieldSpec;
import com.example.springbackendtemplate1.commons.utils.SearchType;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum UnitSearchField implements SearchFieldSpec<UnitFilterRequest> {
    CODE("code", SearchType.LIKE, false, null, UnitFilterRequest::getCode),
    NAME("name", SearchType.LIKE, true, "unitLocaleEntities", UnitFilterRequest::getName);

    private final String fieldName;
    private final SearchType searchType;
    private final boolean localeField;
    private final String collectionField;
    private final Function<UnitFilterRequest, String> valueExtractor;

    UnitSearchField(String fieldName, SearchType searchType, boolean localeField,
                    String collectionField, Function<UnitFilterRequest, String> valueExtractor) {
        this.fieldName = fieldName;
        this.searchType = searchType;
        this.localeField = localeField;
        this.collectionField = collectionField;
        this.valueExtractor = valueExtractor;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values()).map(UnitSearchField::getFieldName).collect(Collectors.toSet());
    }

    public static Set<String> localeSearchFields() {
        return Arrays.stream(values()).filter(UnitSearchField::isLocaleField)
                .map(UnitSearchField::getFieldName).collect(Collectors.toSet());
    }
}
