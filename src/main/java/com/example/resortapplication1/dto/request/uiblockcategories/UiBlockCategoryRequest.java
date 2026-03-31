package com.example.resortapplication1.dto.request.uiblockcategories;

import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UiBlockCategoryRequest {
    private String key;
    private String name;
    private String description;
}
