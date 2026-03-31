package com.example.resortapplication1.dto.response.uiblockdefinitions;

import com.example.resortapplication1.model.dto.UiBlockDefinitionDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UiBlockDefinitionResponse {
    private UiBlockDefinitionDto data;

    public UiBlockDefinitionResponse(UiBlockDefinitionDto uiBlockDefinition) {
        this.data = uiBlockDefinition;
    }
}
