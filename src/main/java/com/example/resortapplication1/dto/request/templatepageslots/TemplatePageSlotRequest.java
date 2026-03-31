package com.example.resortapplication1.dto.request.templatepageslots;

import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TemplatePageSlotRequest {
    private Long templatePageId;
    private Long uiBlockCategoryId;
    private String slotName;
    private Boolean isRequired;
    private Integer slotOrder;
}
