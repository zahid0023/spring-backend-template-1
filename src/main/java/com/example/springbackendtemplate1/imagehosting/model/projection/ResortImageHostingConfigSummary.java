package com.example.springbackendtemplate1.imagehosting.model.projection;

import com.example.springbackendtemplate1.imagehosting.enums.ImageHostingProvider;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Map;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public interface ResortImageHostingConfigSummary {
    Long getId();

    @Value("#{target.resortEntity.id}")
    Long getResortId();

    String getName();
    ImageHostingProvider getProvider();
    Map<String, String> getConfig();
}
