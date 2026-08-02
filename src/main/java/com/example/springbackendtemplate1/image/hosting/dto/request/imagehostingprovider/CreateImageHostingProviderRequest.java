package com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider;

import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.configfield.CreateImageHostingProviderConfigFieldRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateImageHostingProviderRequest extends ImageHostingProviderRequest {

    @NotBlank
    @Size(max = 50)
    private String code;

    @Valid
    @NotEmpty
    private List<CreateImageHostingProviderConfigFieldRequest> configFields;

}
