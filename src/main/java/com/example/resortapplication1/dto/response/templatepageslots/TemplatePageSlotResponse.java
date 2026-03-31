package com.example.resortapplication1.dto.response.templatepageslots;

import com.example.resortapplication1.model.dto.TemplatePageSlotDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TemplatePageSlotResponse {
    private TemplatePageSlotDto data;

    public TemplatePageSlotResponse(TemplatePageSlotDto templatePageSlot) {
        this.data = templatePageSlot;
    }
}
