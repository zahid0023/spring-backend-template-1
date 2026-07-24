package com.example.springbackendtemplate1.currency.dto.request.currency;

import com.example.springbackendtemplate1.currency.dto.request.currency.currencylocale.CreateCurrencyLocaleRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateCurrencyRequest extends CurrencyRequest {

    @Size(max = 3)
    private String numericCode;

    @NotNull
    private Long countryId;

    @Valid
    private List<CreateCurrencyLocaleRequest> locales;
}
