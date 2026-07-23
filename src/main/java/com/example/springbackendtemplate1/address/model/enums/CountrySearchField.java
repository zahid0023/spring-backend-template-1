package com.example.springbackendtemplate1.address.model.enums;

import com.example.springbackendtemplate1.address.dto.request.country.CountryFilterRequest;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum CountrySearchField {
    CODE("code", CountryFilterRequest::getCode),
    ISO3_CODE("iso3Code", CountryFilterRequest::getIso3Code),
    PHONE_CODE("phoneCode", CountryFilterRequest::getPhoneCode);

    private final String fieldName;
    private final Function<CountryFilterRequest, String> valueExtractor;

    CountrySearchField(String fieldName, Function<CountryFilterRequest, String> valueExtractor) {
        this.fieldName = fieldName;
        this.valueExtractor = valueExtractor;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values())
                .map(CountrySearchField::getFieldName)
                .collect(Collectors.toSet());
    }
}
