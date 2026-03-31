package com.example.resortapplication1.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UiBlockDefinitionDto {
    private Long id;
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
