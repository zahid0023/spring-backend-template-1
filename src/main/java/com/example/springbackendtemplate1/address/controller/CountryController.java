package com.example.springbackendtemplate1.address.controller;

import com.example.springbackendtemplate1.address.dto.request.city.CityFilterRequest;
import com.example.springbackendtemplate1.address.dto.request.country.CountryFilterRequest;
import com.example.springbackendtemplate1.address.dto.request.country.CreateCountryRequest;
import com.example.springbackendtemplate1.address.dto.request.country.UpdateCountryRequest;
import com.example.springbackendtemplate1.address.dto.request.country.countrylocale.CreateCountryLocaleRequest;
import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.address.service.CityService;
import com.example.springbackendtemplate1.commons.utils.LocaleUtils;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.address.service.CountryService;
import com.example.springbackendtemplate1.locale.service.LocaleService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/countries")
public class CountryController {

    private final CountryService countryService;
    private final CityService cityService;
    private final LocaleService localeService;

    public CountryController(CountryService countryService,
                             CityService cityService,
                             LocaleService localeService) {
        this.countryService = countryService;
        this.cityService = cityService;
        this.localeService = localeService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateCountryRequest request) {
        Map<Long, LocaleEntity> localeEntityMap = LocaleUtils.resolveLocaleMap(
                request.getLocales(), CreateCountryLocaleRequest::getLocaleId, localeService);
        return ResponseEntity.status(HttpStatus.CREATED).body(countryService.create(request, localeEntityMap));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(countryService.getById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAll(@Valid @ParameterObject CountryFilterRequest request) {
        return ResponseEntity.ok(countryService.getAll(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCountryRequest request) {
        CountryEntity entity = countryService.getEntityById(id);
        return ResponseEntity.ok(countryService.update(entity, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        CountryEntity entity = countryService.getEntityById(id);
        return ResponseEntity.ok(countryService.delete(entity));
    }

    @GetMapping("/{country-id}/cities")
    public ResponseEntity<?> getCitiesByCountry(
            @PathVariable("country-id") Long countryId,
            @Valid @ParameterObject CityFilterRequest request) {
        return ResponseEntity.ok(cityService.getAll(request, countryId));
    }
}
