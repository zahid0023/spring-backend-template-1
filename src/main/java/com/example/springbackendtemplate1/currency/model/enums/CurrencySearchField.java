package com.example.springbackendtemplate1.currency.model.enums;

import com.example.springbackendtemplate1.currency.dto.request.currency.CurrencyFilterRequest;
import com.example.springbackendtemplate1.commons.utils.SearchFieldSpec;
import com.example.springbackendtemplate1.commons.utils.SearchType;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum CurrencySearchField implements SearchFieldSpec<CurrencyFilterRequest> {
    CODE("code", SearchType.LIKE, false, null, CurrencyFilterRequest::getCode),
    NUMERIC_CODE("numericCode", SearchType.LIKE, false, null, CurrencyFilterRequest::getNumericCode),
    NAME("name", SearchType.LIKE, true, "currencyLocaleEntities", CurrencyFilterRequest::getName),
    SHORT_NAME("shortName", SearchType.LIKE, true, "currencyLocaleEntities", CurrencyFilterRequest::getShortName);

    private final String fieldName;
    private final SearchType searchType;
    private final boolean localeField;
    private final String collectionField;
    private final Function<CurrencyFilterRequest, String> valueExtractor;

    CurrencySearchField(String fieldName, SearchType searchType, boolean localeField,
                        String collectionField, Function<CurrencyFilterRequest, String> valueExtractor) {
        this.fieldName = fieldName;
        this.searchType = searchType;
        this.localeField = localeField;
        this.collectionField = collectionField;
        this.valueExtractor = valueExtractor;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values()).map(CurrencySearchField::getFieldName).collect(Collectors.toSet());
    }

    public static Set<String> localeSearchFields() {
        return Arrays.stream(values()).filter(CurrencySearchField::isLocaleField)
                .map(CurrencySearchField::getFieldName).collect(Collectors.toSet());
    }
}
