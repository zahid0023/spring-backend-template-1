package com.example.resortapplication1.dto.response.templatepages;

import com.example.resortapplication1.model.dto.TemplatePageDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TemplatePageResponse {
    private TemplatePageDto data;

    public TemplatePageResponse(TemplatePageDto templatePage) {
        this.data = templatePage;
    }
}
