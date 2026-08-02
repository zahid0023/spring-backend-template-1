package com.example.springbackendtemplate1.image.hosting.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImageHostingProviderDto {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer sortOrder;

    @Builder.Default
    private List<ImageHostingProviderConfigFieldDto> configFields = new ArrayList<>();

    @Builder.Default
    private List<ImageHostingConfigDto> configs = new ArrayList<>();
}
