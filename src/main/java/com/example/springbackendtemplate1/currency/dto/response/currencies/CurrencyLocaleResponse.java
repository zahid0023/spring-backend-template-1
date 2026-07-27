package com.example.springbackendtemplate1.currency.dto.response.currencies;

import com.example.springbackendtemplate1.currency.model.dto.CurrencyLocaleDto;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CurrencyLocaleResponse {
    private final CurrencyLocaleDto currencyLocale;

    public CurrencyLocaleResponse(CurrencyLocaleDto currencyLocale) {
        this.currencyLocale = currencyLocale;
    }
}
