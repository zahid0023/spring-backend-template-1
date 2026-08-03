package com.example.springbackendtemplate1.image.upload.dto.response;

import lombok.Builder;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImageUploadResponse {
    private String imageUrl;
    private String publicId;
}
