package com.example.resortapplication1.dto.request.templatepageslotvariants;

import lombok.Data;
import lombok.EqualsAndHashCode;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateTemplatePageSlotVariantRequest extends TemplatePageSlotVariantRequest {
}
