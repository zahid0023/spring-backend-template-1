package com.example.resortapplication1.dto.response.templates;

import com.example.resortapplication1.model.dto.TemplateDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TemplateResponse {
    private TemplateDto data;

    public TemplateResponse(TemplateDto template) {
        this.data = template;
    }
}
