package com.example.springbackendtemplate1.commons.dto.response;

import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ApiErrorResponse {
    private final String requestId;
    private final int status;
    private final String error;
    private final String message;

    public ApiErrorResponse(String requestId, int status, String error, String message) {
        this.requestId = requestId;
        this.status = status;
        this.error = error;
        this.message = message;
    }
}
