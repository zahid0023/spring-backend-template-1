package com.example.springbackendtemplate1.address.controller;

import com.example.springbackendtemplate1.address.dto.request.city.locale.CreateCityLocaleRequest;
import com.example.springbackendtemplate1.address.dto.request.city.locale.UpdateCityLocaleRequest;
import com.example.springbackendtemplate1.address.model.entity.CityEntity;
import com.example.springbackendtemplate1.address.model.entity.CityLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.address.service.CityLocaleService;
import com.example.springbackendtemplate1.address.service.CityService;
import com.example.springbackendtemplate1.locale.service.LocaleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cities/{city-id}/locales")
public class CityLocaleController {

    private final CityService cityService;
    private final CityLocaleService cityLocaleService;
    private final LocaleService localeService;

    public CityLocaleController(CityService cityService,
                                CityLocaleService cityLocaleService,
                                LocaleService localeService) {
        this.cityService = cityService;
        this.cityLocaleService = cityLocaleService;
        this.localeService = localeService;
    }

    @PostMapping
    public ResponseEntity<?> create(
            @PathVariable("city-id") Long cityId,
            @Valid @RequestBody CreateCityLocaleRequest request) {
        CityEntity cityEntity = cityService.getEntityById(cityId);
        LocaleEntity localeEntity = localeService.getEntityById(request.getLocaleId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cityLocaleService.create(request, cityEntity, localeEntity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable("city-id") Long cityId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateCityLocaleRequest request) {
        CityLocaleEntity entity = cityLocaleService.getEntityById(cityId, id);
        return ResponseEntity.ok(cityLocaleService.update(entity, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable("city-id") Long cityId,
            @PathVariable Long id) {
        CityLocaleEntity entity = cityLocaleService.getEntityById(cityId, id);
        return ResponseEntity.ok(cityLocaleService.delete(entity));
    }
}
