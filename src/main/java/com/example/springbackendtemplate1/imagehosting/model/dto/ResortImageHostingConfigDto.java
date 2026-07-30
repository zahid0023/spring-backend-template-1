package com.example.springbackendtemplate1.imagehosting.model.dto;

import com.example.springbackendtemplate1.imagehosting.enums.ImageHostingProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResortImageHostingConfigDto {
    private Long id;
    private Long resortId;
    private String name;
    private ImageHostingProvider provider;
    private Map<String, String> config;
}
