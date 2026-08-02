package com.example.springbackendtemplate1.address.serviceImpl;

import com.example.springbackendtemplate1.address.model.entity.CityLocaleEntity;
import com.example.springbackendtemplate1.address.model.mapper.CityLocaleMapper;
import com.example.springbackendtemplate1.commons.context.LocaleContext;
import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.commons.utils.Pagination;
import com.example.springbackendtemplate1.address.dto.request.city.CityFilterRequest;
import com.example.springbackendtemplate1.address.dto.request.city.CreateCityRequest;
import com.example.springbackendtemplate1.address.dto.request.city.UpdateCityRequest;
import com.example.springbackendtemplate1.address.dto.response.cities.CityResponse;
import com.example.springbackendtemplate1.address.model.dto.CityDto;
import com.example.springbackendtemplate1.address.model.entity.CityEntity;
import com.example.springbackendtemplate1.address.model.dto.CountryDto;
import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.address.model.enums.CitySearchField;
import com.example.springbackendtemplate1.address.model.enums.CitySortField;
import com.example.springbackendtemplate1.address.model.mapper.CityMapper;
import com.example.springbackendtemplate1.address.model.mapper.CountryMapper;
import com.example.springbackendtemplate1.address.repository.CityRepository;
import com.example.springbackendtemplate1.address.service.CityService;
import com.example.springbackendtemplate1.address.specification.CitySpecification;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                                  LocaleEntity localeEntity) {
        if (cityRepository.existsByCodeAndIsActiveAndIsDeleted(request.getCode(), true, false)) {
            throw new IllegalStateException("City with code '" + request.getCode() + "' already exists");
        }

        CityEntity entity = CityMapper.create(request);
        countryEntity.addCityEntity(entity);

        CityLocaleEntity cityLocaleEntity = CityLocaleMapper.create(request.getLocale());
        localeEntity.addCityLocaleEntity(cityLocaleEntity);
        
        entity.addCityLocaleEntity(cityLocaleEntity);

        cityRepository.save(entity);
        log.info("City created with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public CityEntity getEntityById(Long id) {
        return cityRepository.findByIdAndIsActiveAndIsDeleted(id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("City not found with id: " + id));
    }

    @Override
    public CityResponse getById(Long id) {
        CityEntity entity = getEntityById(id);
        CountryDto country = CountryMapper.toDto(entity.getCountryEntity()).build();
        CityDto dto = CityMapper.toDto(entity)
                .country(country)
                .build();
        return new CityResponse(dto);
    }

    @Override
    public PaginatedResponse<CityDto> getAll(CityFilterRequest request) {
        Specification<@NonNull CityEntity> specification = CitySpecification.filter(request, LocaleContext.getLocaleId());
        Pageable pageable = request.toPageable(ALLOWED_SORT_FIELDS, CitySortField.localeSortFields());
        Page<@NonNull CityDto> page = cityRepository
                .findAll(specification, pageable)
                .map(entity -> CityMapper.toDto(entity)
                        .country(CountryMapper.toDto(entity.getCountryEntity()).build())
                        .build());
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
}
