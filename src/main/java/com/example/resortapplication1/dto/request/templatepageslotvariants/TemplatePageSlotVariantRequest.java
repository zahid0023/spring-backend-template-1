package com.example.resortapplication1.dto.request.templatepageslotvariants;

import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TemplatePageSlotVariantRequest {
    private Long templatePageSlotId;
    private Long uiBlockDefinitionId;
    private Integer displayOrder;
    private Boolean isDefault;
}
