package com.example.springbackendtemplate1.currency.dto.request.currency.currencylocale;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateCurrencyLocaleRequest extends CurrencyLocaleRequest {

    @NotNull
    private Long localeId;
}
