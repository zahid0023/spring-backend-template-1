package com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingproviderconfig;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.Map;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImageHostingProviderConfigRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private Map<String, Object> config;
}
