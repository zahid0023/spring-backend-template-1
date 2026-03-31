package com.example.resortapplication1.dto.response.uiblockcategories;

import com.example.resortapplication1.model.dto.UiBlockCategoryDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UiBlockCategoryResponse {
    private UiBlockCategoryDto data;

    public UiBlockCategoryResponse(UiBlockCategoryDto uiBlockCategory) {
        this.data = uiBlockCategory;
    }
}
