package com.example.springbackendtemplate1.unit.model.enums;

import com.example.springbackendtemplate1.unit.dto.request.unittype.UnitTypeFilterRequest;
import com.example.springbackendtemplate1.commons.utils.SearchFieldSpec;
import com.example.springbackendtemplate1.commons.utils.SearchType;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum UnitTypeSearchField implements SearchFieldSpec<UnitTypeFilterRequest> {
    CODE("code", SearchType.LIKE, false, null, UnitTypeFilterRequest::getCode),
    NAME("name", SearchType.LIKE, true, "unitTypeLocaleEntities", UnitTypeFilterRequest::getName);

    private final String fieldName;
    private final SearchType searchType;
    private final boolean localeField;
    private final String collectionField;
    private final Function<UnitTypeFilterRequest, String> valueExtractor;

    UnitTypeSearchField(String fieldName, SearchType searchType, boolean localeField,
                    String collectionField, Function<UnitTypeFilterRequest, String> valueExtractor) {
        this.fieldName = fieldName;
        this.searchType = searchType;
        this.localeField = localeField;
        this.collectionField = collectionField;
        this.valueExtractor = valueExtractor;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values()).map(UnitTypeSearchField::getFieldName).collect(Collectors.toSet());
    }

    public static Set<String> localeSearchFields() {
        return Arrays.stream(values()).filter(UnitTypeSearchField::isLocaleField)
                .map(UnitTypeSearchField::getFieldName).collect(Collectors.toSet());
    }
}
