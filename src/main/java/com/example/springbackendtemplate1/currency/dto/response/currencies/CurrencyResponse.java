package com.example.springbackendtemplate1.currency.dto.response.currencies;

import com.example.springbackendtemplate1.currency.model.dto.CurrencyDto;
import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CurrencyResponse {
    private final CurrencyDto currency;

    public CurrencyResponse(CurrencyDto currency) {
        this.currency = currency;
    }
}
