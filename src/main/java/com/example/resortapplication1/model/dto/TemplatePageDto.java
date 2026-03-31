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
public class TemplatePageDto {
    private Long id;
    private Long templateId;
    private Long pageTypeId;
    private String pageName;
    private String pageSlug;
    private Integer pageOrder;
    private List<TemplatePageSlotDto> templatePageSlots;
}
