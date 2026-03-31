package com.example.resortapplication1.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TemplatePageSlotDto {
    private Long id;
    private Long templatePageId;
    private Long uiBlockCategoryId;
    private String slotName;
    private Boolean isRequired;
    private Integer slotOrder;
    private List<TemplatePageSlotVariantDto> templatePageSlotVariants;
}
