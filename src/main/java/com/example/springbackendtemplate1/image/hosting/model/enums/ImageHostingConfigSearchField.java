package com.example.springbackendtemplate1.image.hosting.model.enums;

import com.example.springbackendtemplate1.commons.utils.SearchType;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingconfig.ImageHostingConfigFilterRequest;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum ImageHostingConfigSearchField {
    NAME("name", SearchType.LIKE, ImageHostingConfigFilterRequest::getName);

    private final String fieldName;
    private final SearchType searchType;
    private final Function<ImageHostingConfigFilterRequest, String> valueExtractor;

    ImageHostingConfigSearchField(String fieldName, SearchType searchType,
                                  Function<ImageHostingConfigFilterRequest, String> valueExtractor) {
        this.fieldName = fieldName;
        this.searchType = searchType;
        this.valueExtractor = valueExtractor;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values()).map(ImageHostingConfigSearchField::getFieldName).collect(Collectors.toSet());
    }
}
