package com.example.springbackendtemplate1.address.model.enums;

import com.example.springbackendtemplate1.address.dto.request.country.CountryFilterRequest;
import com.example.springbackendtemplate1.commons.utils.SearchType;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum CountrySearchField {
    // direct entity String fields:
    ISO3_CODE("iso3Code", SearchType.LIKE, false, null, CountryFilterRequest::getIso3Code),
    PHONE_CODE("phoneCode", SearchType.LIKE, false, null, CountryFilterRequest::getPhoneCode),
    // locale child String fields (require JOIN):
    NAME("name", SearchType.LIKE, true, "countryLocaleEntities", CountryFilterRequest::getName);

    private final String fieldName;
    private final SearchType searchType;
    private final boolean localeField;
    private final String collectionField;
    private final Function<CountryFilterRequest, String> valueExtractor;

    CountrySearchField(String fieldName, SearchType searchType, boolean localeField,
                       String collectionField,
                       Function<CountryFilterRequest, String> valueExtractor) {
        this.fieldName = fieldName;
        this.searchType = searchType;
        this.localeField = localeField;
        this.collectionField = collectionField;
        this.valueExtractor = valueExtractor;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values())
                .map(CountrySearchField::getFieldName)
                .collect(Collectors.toSet());
    }

    public static Set<String> localeSearchFields() {
        return Arrays.stream(values())
                .filter(CountrySearchField::isLocaleField)
                .map(CountrySearchField::getFieldName)
                .collect(Collectors.toSet());
    }
}
