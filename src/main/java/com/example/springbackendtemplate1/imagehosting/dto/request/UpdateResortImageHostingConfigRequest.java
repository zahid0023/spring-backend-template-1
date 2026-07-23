package com.example.springbackendtemplate1.imagehosting.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateResortImageHostingConfigRequest {

    @NotNull
    private String name;
}
