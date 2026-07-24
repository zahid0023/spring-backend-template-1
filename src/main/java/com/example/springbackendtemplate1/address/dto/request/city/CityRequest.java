package com.example.springbackendtemplate1.address.dto.request.city;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CityRequest {

    @Size(max = 50)
    private String code;

    @NotNull
    private Integer sortOrder;

}
