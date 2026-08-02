package com.example.springbackendtemplate1.image.hosting.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImageHostingProviderConfigFieldDto {
    private Long id;
    private String key;
    private String label;
    private String fieldType;
    private String placeholder;
    private String defaultValue;
    private Boolean isRequired;
    private Integer sortOrder;
}
