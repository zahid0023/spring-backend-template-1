package com.example.springbackendtemplate1.auth.dto.request.permission;

import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AssignPermissionRequest {
    private Set<Long> permissionIds;
}
