package com.example.springbackendtemplate1.imagehosting.dto.request;

import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImageMetaRequest {
    private String clientImageId;

    private String caption;
    private Boolean isDefault = false;
    private Integer sortOrder = 0;
}