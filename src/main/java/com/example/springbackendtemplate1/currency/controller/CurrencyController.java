package com.example.springbackendtemplate1.currency.controller;

import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.address.service.CountryService;
import com.example.springbackendtemplate1.commons.utils.LocaleUtils;
import com.example.springbackendtemplate1.currency.dto.request.currency.CreateCurrencyRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.CurrencyFilterRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.UpdateCurrencyRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.currencylocale.CreateCurrencyLocaleRequest;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyEntity;
import com.example.springbackendtemplate1.currency.service.CurrencyService;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.locale.service.LocaleService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;
    private final CountryService countryService;
    private final LocaleService localeService;

    public CurrencyController(CurrencyService currencyService,
                               CountryService countryService,
                               LocaleService localeService) {
        this.currencyService = currencyService;
        this.countryService = countryService;
        this.localeService = localeService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateCurrencyRequest request) {
        CountryEntity countryEntity = countryService.getEntityById(request.getCountryId());
        Map<Long, LocaleEntity> localeEntityMap = LocaleUtils.resolveLocaleMap(
                request.getLocales(), CreateCurrencyLocaleRequest::getLocaleId, localeService);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(currencyService.create(request, countryEntity, localeEntityMap));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(currencyService.getById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAll(@Valid @ParameterObject CurrencyFilterRequest request) {
        return ResponseEntity.ok(currencyService.getAll(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCurrencyRequest request) {
        CurrencyEntity entity = currencyService.getEntityById(id);
        return ResponseEntity.ok(currencyService.update(entity, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return ResponseEntity.ok(currencyService.delete(id));
    }
}
