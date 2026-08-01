package com.example.springbackendtemplate1.currency.dto.request.currency;

import com.example.springbackendtemplate1.currency.dto.request.currency.locale.CurrencyLocaleRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateCurrencyRequest extends CurrencyRequest {

    @NotBlank
    @Size(max = 3)
    @Pattern(regexp = "^[A-Z]{3}$")
    private String code;

    @NotBlank
    @Size(max = 3)
    @Pattern(regexp = "^[0-9]{3}$")
    private String numericCode;

    @NotNull
    private Long countryId;

    @Valid
    @NotNull
    private CurrencyLocaleRequest locale;
}
