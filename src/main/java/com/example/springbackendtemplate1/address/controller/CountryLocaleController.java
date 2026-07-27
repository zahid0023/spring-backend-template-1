package com.example.springbackendtemplate1.address.controller;

import com.example.springbackendtemplate1.address.dto.request.country.countrylocale.CreateCountryLocaleRequest;
import com.example.springbackendtemplate1.address.dto.request.country.countrylocale.UpdateCountryLocaleRequest;
import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.address.model.entity.CountryLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.address.service.CountryLocaleService;
import com.example.springbackendtemplate1.address.service.CountryService;
import com.example.springbackendtemplate1.locale.service.LocaleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/countries/{country-id}/locales")
public class CountryLocaleController {

    private final CountryService countryService;
    private final CountryLocaleService countryLocaleService;
    private final LocaleService localeService;

    public CountryLocaleController(CountryService countryService,
                                   CountryLocaleService countryLocaleService,
                                   LocaleService localeService) {
        this.countryService = countryService;
        this.countryLocaleService = countryLocaleService;
        this.localeService = localeService;
    }

    @PostMapping
    public ResponseEntity<?> create(
            @PathVariable("country-id") Long countryId,
            @Valid @RequestBody CreateCountryLocaleRequest request) {
        CountryEntity countryEntity = countryService.getEntityById(countryId);
        LocaleEntity localeEntity = localeService.getEntityById(request.getLocaleId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(countryLocaleService.create(request, countryEntity, localeEntity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable("country-id") Long countryId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateCountryLocaleRequest request) {
        CountryLocaleEntity entity = countryLocaleService.getEntityById(countryId, id);
        return ResponseEntity.ok(countryLocaleService.update(entity, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable("country-id") Long countryId,
            @PathVariable Long id) {
        CountryLocaleEntity entity = countryLocaleService.getEntityById(countryId, id);
        return ResponseEntity.ok(countryLocaleService.delete(entity));
    }
}
