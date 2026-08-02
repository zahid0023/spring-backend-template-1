package com.example.springbackendtemplate1.image.hosting.model.enums;

import com.example.springbackendtemplate1.commons.utils.SearchType;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingproviderconfig.ImageHostingProviderConfigFilterRequest;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum ImageHostingProviderConfigSearchField {
    NAME("name", SearchType.LIKE, ImageHostingProviderConfigFilterRequest::getName);

    private final String fieldName;
    private final SearchType searchType;
    private final Function<ImageHostingProviderConfigFilterRequest, String> valueExtractor;

    ImageHostingProviderConfigSearchField(String fieldName, SearchType searchType,
                                  Function<ImageHostingProviderConfigFilterRequest, String> valueExtractor) {
        this.fieldName = fieldName;
        this.searchType = searchType;
        this.valueExtractor = valueExtractor;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values()).map(ImageHostingProviderConfigSearchField::getFieldName).collect(Collectors.toSet());
    }
}
