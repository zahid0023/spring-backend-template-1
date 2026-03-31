package com.example.resortapplication1.dto.response.pagetypes;

import com.example.resortapplication1.model.dto.PageTypeDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PageTypeResponse {
    private PageTypeDto data;

    public PageTypeResponse(PageTypeDto pageType) {
        this.data = pageType;
    }
}
