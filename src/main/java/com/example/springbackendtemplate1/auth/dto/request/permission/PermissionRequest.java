package com.example.springbackendtemplate1.auth.dto.request.permission;

import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PermissionRequest {
    private String name;
    private String description;
}
