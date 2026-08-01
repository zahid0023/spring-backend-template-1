package com.example.springbackendtemplate1.address.serviceImpl;

import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.address.dto.request.city.locale.CreateCityLocaleRequest;
import com.example.springbackendtemplate1.address.dto.request.city.locale.UpdateCityLocaleRequest;
import com.example.springbackendtemplate1.address.model.entity.CityEntity;
import com.example.springbackendtemplate1.address.model.entity.CityLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.address.model.mapper.CityLocaleMapper;
import com.example.springbackendtemplate1.address.repository.CityLocaleRepository;
import com.example.springbackendtemplate1.address.service.CityLocaleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class CityLocaleServiceImpl implements CityLocaleService {
    private final CityLocaleRepository cityLocaleRepository;

    public CityLocaleServiceImpl(CityLocaleRepository cityLocaleRepository) {
        this.cityLocaleRepository = cityLocaleRepository;
    }

    @Transactional
    @Override
    public SuccessResponse create(CreateCityLocaleRequest request,
                                  CityEntity cityEntity,
                                  LocaleEntity localeEntity) {
        if (cityLocaleRepository.existsByCityEntity_IdAndLocaleEntity_IdAndIsActiveAndIsDeleted(
                cityEntity.getId(), localeEntity.getId(), true, false)) {
            throw new IllegalStateException("City already has a locale entry for locale id: " + localeEntity.getId());
        }

        CityLocaleEntity entity = CityLocaleMapper.create(request);
        cityEntity.addCityLocaleEntity(entity);
        localeEntity.addCityLocaleEntity(entity);
        cityLocaleRepository.save(entity);
        log.info("CityLocale created with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional
    @Override
    public SuccessResponse update(CityLocaleEntity entity,
                                  UpdateCityLocaleRequest request) {
        CityLocaleMapper.update(entity, request);
        cityLocaleRepository.save(entity);
        log.info("CityLocale updated with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional
    @Override
    public SuccessResponse delete(CityLocaleEntity entity) {
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        cityLocaleRepository.save(entity);
        log.info("CityLocale soft-deleted with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public CityLocaleEntity getEntityById(Long cityId, Long id) {
        return cityLocaleRepository
                .findByCityEntity_IdAndIdAndIsActiveAndIsDeleted(cityId, id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("CityLocale not found with id: " + id));
    }
}
