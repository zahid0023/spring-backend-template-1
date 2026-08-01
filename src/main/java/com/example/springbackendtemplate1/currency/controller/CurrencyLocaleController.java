package com.example.springbackendtemplate1.currency.controller;

import com.example.springbackendtemplate1.currency.dto.request.currency.locale.CreateCurrencyLocaleRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.locale.UpdateCurrencyLocaleRequest;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyEntity;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.currency.service.CurrencyLocaleService;
import com.example.springbackendtemplate1.currency.service.CurrencyService;
import com.example.springbackendtemplate1.locale.service.LocaleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/currencies/{currency-id}/locales")
public class CurrencyLocaleController {

    private final CurrencyService currencyService;
    private final CurrencyLocaleService currencyLocaleService;
    private final LocaleService localeService;

    public CurrencyLocaleController(CurrencyService currencyService,
                                    CurrencyLocaleService currencyLocaleService,
                                    LocaleService localeService) {
        this.currencyService = currencyService;
        this.currencyLocaleService = currencyLocaleService;
        this.localeService = localeService;
    }

    @PostMapping
    public ResponseEntity<?> create(
            @PathVariable("currency-id") Long currencyId,
            @Valid @RequestBody CreateCurrencyLocaleRequest request) {
        CurrencyEntity currencyEntity = currencyService.getEntityById(currencyId);
        LocaleEntity localeEntity = localeService.getEntityById(request.getLocaleId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(currencyLocaleService.create(request, currencyEntity, localeEntity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable("currency-id") Long currencyId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateCurrencyLocaleRequest request) {
        CurrencyLocaleEntity entity = currencyLocaleService.getEntityById(currencyId, id);
        return ResponseEntity.ok(currencyLocaleService.update(entity, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable("currency-id") Long currencyId,
            @PathVariable Long id) {
        CurrencyLocaleEntity entity = currencyLocaleService.getEntityById(currencyId, id);
        return ResponseEntity.ok(currencyLocaleService.delete(entity));
    }
}
