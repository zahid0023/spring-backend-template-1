package com.example.springbackendtemplate1.imagehosting.model.dto;

import com.example.springbackendtemplate1.imagehosting.enums.ImageHostingProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

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
