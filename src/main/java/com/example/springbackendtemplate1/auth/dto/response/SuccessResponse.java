package com.example.springbackendtemplate1.auth.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

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
