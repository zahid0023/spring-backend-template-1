package com.example.springbackendtemplate1.imagehosting.dto.request;

import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImageRequest {
    private String imageUrl;
    private String publicId;
    private String caption;
    private Boolean isDefault;
    private Integer sortOrder;
}
