package com.example.springbackendtemplate1.imagehosting.dto.request;

import com.example.springbackendtemplate1.imagehosting.enums.ImageHostingProvider;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Map;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImageHostingConfigRequest {
    @NotNull(message = "Name must not be null")
    private String name;

    @NotNull(message = "Provider must not be null")
    private ImageHostingProvider provider;

    @NotNull(message = "Config must not be null")
    private Map<String, String> config;
}
