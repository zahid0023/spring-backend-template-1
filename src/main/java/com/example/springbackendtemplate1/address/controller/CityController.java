package com.example.springbackendtemplate1.address.controller;

import com.example.springbackendtemplate1.address.dto.request.city.CityFilterRequest;
import com.example.springbackendtemplate1.address.dto.request.city.CreateCityRequest;
import com.example.springbackendtemplate1.address.dto.request.city.UpdateCityRequest;
import com.example.springbackendtemplate1.address.dto.request.city.citylocale.CreateCityLocaleRequest;
import com.example.springbackendtemplate1.address.model.entity.CityEntity;
import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.commons.utils.LocaleUtils;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.address.service.CityService;
import com.example.springbackendtemplate1.address.service.CountryService;
import com.example.springbackendtemplate1.locale.service.LocaleService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/cities")
public class CityController {

    private final CityService cityService;
    private final CountryService countryService;
    private final LocaleService localeService;

    public CityController(CityService cityService,
                          CountryService countryService,
                          LocaleService localeService) {
        this.cityService = cityService;
        this.countryService = countryService;
        this.localeService = localeService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateCityRequest request) {
        Map<Long, LocaleEntity> localeEntityMap = LocaleUtils.resolveLocaleMap(
                request.getLocales(), CreateCityLocaleRequest::getLocaleId, localeService);
        CountryEntity countryEntity = countryService.getEntityById(request.getCountryId());
        return ResponseEntity.status(HttpStatus.CREATED).body(cityService.create(request, countryEntity, localeEntityMap));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(cityService.getById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAll(@Valid @ParameterObject CityFilterRequest request,
                                    @RequestParam(required = false) Long countryId) {
        return ResponseEntity.ok(cityService.getAll(request, countryId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCityRequest request) {
        CityEntity entity = cityService.getEntityById(id);
        return ResponseEntity.ok(cityService.update(entity, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        CityEntity entity = cityService.getEntityById(id);
        return ResponseEntity.ok(cityService.delete(entity));
    }
}
