package com.example.springbackendtemplate1.auth.dto.response;

import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CurrentTimeResponse {
    private final String currentTime;

    public CurrentTimeResponse(String currentTime) {
        this.currentTime = currentTime;
    }
}
