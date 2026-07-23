package com.example.springbackendtemplate1.locale.dto.response.locales;

import com.example.springbackendtemplate1.locale.model.dto.LocaleDto;
import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LocaleResponse {
    private final LocaleDto locale;

    public LocaleResponse(LocaleDto locale) {
        this.locale = locale;
    }
}
