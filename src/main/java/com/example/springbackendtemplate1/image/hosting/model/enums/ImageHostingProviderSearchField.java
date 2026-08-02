package com.example.springbackendtemplate1.image.hosting.model.enums;

import com.example.springbackendtemplate1.commons.utils.SearchType;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.ImageHostingProviderFilterRequest;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum ImageHostingProviderSearchField {
    CODE("code", SearchType.LIKE, ImageHostingProviderFilterRequest::getCode),
    NAME("name", SearchType.LIKE, ImageHostingProviderFilterRequest::getName);

    private final String fieldName;
    private final SearchType searchType;
    private final Function<ImageHostingProviderFilterRequest, String> valueExtractor;

    ImageHostingProviderSearchField(String fieldName, SearchType searchType,
                                    Function<ImageHostingProviderFilterRequest, String> valueExtractor) {
        this.fieldName = fieldName;
        this.searchType = searchType;
        this.valueExtractor = valueExtractor;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values()).map(ImageHostingProviderSearchField::getFieldName).collect(Collectors.toSet());
    }
}
