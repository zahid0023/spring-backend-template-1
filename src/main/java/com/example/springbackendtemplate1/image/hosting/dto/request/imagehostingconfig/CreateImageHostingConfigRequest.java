package com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingconfig;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateImageHostingConfigRequest extends ImageHostingConfigRequest {

    @NotNull
    private Long imageHostingProviderId;
}
