package com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.configfield;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImageHostingProviderConfigFieldRequest {

    @NotBlank
    @Size(max = 100)
    private String label;

    @NotBlank
    @Size(max = 30)
    private String fieldType;

    @NotNull
    @Size(max = 255)
    private String placeholder;

    @NotNull
    @Size(max = 500)
    private String defaultValue;

    @NotNull
    private Boolean isRequired;

    @NotNull
    private Integer sortOrder;

}
