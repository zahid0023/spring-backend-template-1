package com.example.springbackendtemplate1.address.serviceImpl;

import com.example.springbackendtemplate1.address.model.entity.CityLocaleEntity;
import com.example.springbackendtemplate1.address.model.mapper.CityLocaleMapper;
import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.address.dto.request.city.CityFilterRequest;
import com.example.springbackendtemplate1.address.dto.request.city.CreateCityRequest;
import com.example.springbackendtemplate1.address.dto.request.city.UpdateCityRequest;
import com.example.springbackendtemplate1.address.dto.response.cities.CityResponse;
import com.example.springbackendtemplate1.address.model.dto.CityDto;
import com.example.springbackendtemplate1.address.model.entity.CityEntity;
import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.commons.utils.Pagination;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.address.model.enums.CitySearchField;
import com.example.springbackendtemplate1.address.model.enums.CitySortField;
import com.example.springbackendtemplate1.address.model.mapper.CityMapper;
import com.example.springbackendtemplate1.address.repository.CityRepository;
import com.example.springbackendtemplate1.address.service.CityService;
import com.example.springbackendtemplate1.address.specification.CitySpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class CityServiceImpl implements CityService {

    private static final Set<String> ALLOWED_SORT_FIELDS = CitySortField.allowedFields();
    private static final Set<String> ALLOWED_SEARCH_FIELDS = CitySearchField.allowedFields();

    private final CityRepository cityRepository;

    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Transactional
    @Override
    public SuccessResponse create(CreateCityRequest request,
                                  CountryEntity countryEntity,
                                  Map<Long, LocaleEntity> localeEntityMap) {
        CityEntity entity = CityMapper.create(request);
        countryEntity.addCityEntity(entity);
        request.getLocales().forEach(localeReq -> {
            LocaleEntity localeEntity = localeEntityMap.get(localeReq.getLocaleId());
            CityLocaleEntity locale = CityLocaleMapper.create(localeReq, localeEntity);
            entity.addCityLocaleEntity(locale);
        });
        cityRepository.save(entity);
        log.info("City created with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public CityResponse getById(Long id) {
        CityEntity entity = getEntityById(id);
        CityDto dto = CityMapper.toDto(entity, false);
        return new CityResponse(dto);
    }

    @Override
    public PaginatedResponse<CityDto> getAll(CityFilterRequest request, Long countryId) {
        Page<@NonNull CityDto> page = cityRepository
                .findAll(CitySpecification.filter(request, countryId), request.toPageable(ALLOWED_SORT_FIELDS))
                .map(entity -> CityMapper.toDto(entity, false));
        return Pagination.buildPaginatedResponse(page, ALLOWED_SORT_FIELDS, ALLOWED_SEARCH_FIELDS);
    }

    @Transactional
    @Override
    public SuccessResponse update(CityEntity entity, UpdateCityRequest request) {
        CityMapper.update(entity, request);
        cityRepository.save(entity);
        log.info("City updated with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional
    @Override
    public SuccessResponse delete(CityEntity entity) {
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        cityRepository.save(entity);
        log.info("City soft-deleted with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public CityEntity getEntityById(Long id) {
        return cityRepository.findByIdAndIsActiveAndIsDeleted(id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("City not found with id: " + id));
    }
}
