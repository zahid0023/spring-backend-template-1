package com.example.springbackendtemplate1.currency.dto.response.currencies;

import com.example.springbackendtemplate1.currency.model.dto.CurrencyDto;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CurrencyResponse {

    private final CurrencyDto data;

    public CurrencyResponse(CurrencyDto data) {
        this.data = data;
    }
}
