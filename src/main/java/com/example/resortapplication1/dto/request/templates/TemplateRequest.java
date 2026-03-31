package com.example.resortapplication1.dto.request.templates;

import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TemplateRequest {
    private String key;
    private String name;
    private String description;
    private String status;
}
