package com.example.springbackendtemplate1.imagehosting.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateResortImageHostingConfigRequest {

    @NotNull
    private String name;
}
