package com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingconfig;

import lombok.Data;
import lombok.EqualsAndHashCode;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateImageHostingConfigRequest extends ImageHostingConfigRequest {
}
