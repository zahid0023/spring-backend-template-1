package com.example.springbackendtemplate1.image.upload.dto.response;

import lombok.Builder;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImageBatchUploadResponse {
    private Boolean success;
    private List<ImageUploadResponse> images;
}
