package com.example.springbackendtemplate1.auth.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ForgotPasswordResponse {
    private final String token;

    public ForgotPasswordResponse(String token) {
        this.token = token;
    }
}
