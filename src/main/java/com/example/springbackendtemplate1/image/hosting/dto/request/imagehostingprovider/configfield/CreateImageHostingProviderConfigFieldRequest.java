package com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.configfield;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateImageHostingProviderConfigFieldRequest extends ImageHostingProviderConfigFieldRequest {

    @NotBlank
    @Size(max = 100)
    private String key;

}
