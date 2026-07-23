package com.example.springbackendtemplate1.imagehosting.dto.response;

import com.example.springbackendtemplate1.imagehosting.model.dto.ResortImageHostingConfigDto;
import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResortImageHostingConfigResponse {
    private final ResortImageHostingConfigDto resortImageHostingConfig;

    public ResortImageHostingConfigResponse(ResortImageHostingConfigDto resortImageHostingConfig) {
        this.resortImageHostingConfig = resortImageHostingConfig;
    }
}
