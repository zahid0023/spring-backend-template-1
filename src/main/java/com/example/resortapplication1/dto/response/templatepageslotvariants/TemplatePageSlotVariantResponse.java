package com.example.resortapplication1.dto.response.templatepageslotvariants;

import com.example.resortapplication1.model.dto.TemplatePageSlotVariantDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TemplatePageSlotVariantResponse {
    private TemplatePageSlotVariantDto data;

    public TemplatePageSlotVariantResponse(TemplatePageSlotVariantDto templatePageSlotVariant) {
        this.data = templatePageSlotVariant;
    }
}
