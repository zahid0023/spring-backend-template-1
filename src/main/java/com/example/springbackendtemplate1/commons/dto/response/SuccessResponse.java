package com.example.springbackendtemplate1.commons.dto.response;

import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SuccessResponse {
    private final Boolean success;
    private final Long id;

    public SuccessResponse(final Boolean success, final Long id) {
        this.success = success;
        this.id = id;
    }
}
