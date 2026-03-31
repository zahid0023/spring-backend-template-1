package com.example.resortapplication1.dto.request.uiblockdefinitions;

import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;
import java.util.Map;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UiBlockDefinitionRequest {
    private String uiBlockKey;
    private String name;
    private String description;
    private String uiBlockVersion;
    private Long uiBlockCategoryId;
    private Long pageTypeId;
    private Map<String, Object> editableSchema;
    private Map<String, Object> defaultContent;
    private List<String> allowedPages;
    private String status;
}
