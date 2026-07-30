package com.example.springbackendtemplate1.address.serviceImpl;

import com.example.springbackendtemplate1.address.model.entity.CountryLocaleEntity;
import com.example.springbackendtemplate1.address.model.mapper.CountryLocaleMapper;
import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.commons.utils.Pagination;
import com.example.springbackendtemplate1.address.dto.request.country.CountryFilterRequest;
import com.example.springbackendtemplate1.address.dto.request.country.CreateCountryRequest;
import com.example.springbackendtemplate1.address.dto.request.country.UpdateCountryRequest;
import com.example.springbackendtemplate1.address.dto.response.countries.CountryResponse;
import com.example.springbackendtemplate1.address.model.dto.CountryDto;
import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.address.model.enums.CountrySearchField;
import com.example.springbackendtemplate1.address.model.enums.CountrySortField;
import com.example.springbackendtemplate1.address.model.mapper.CountryMapper;
import com.example.springbackendtemplate1.address.repository.CountryRepository;
import com.example.springbackendtemplate1.address.service.CountryService;
import com.example.springbackendtemplate1.address.specification.CountrySpecification;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
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
public class CountryServiceImpl implements CountryService {

    private static final Set<String> ALLOWED_SORT_FIELDS = CountrySortField.allowedFields();
    private static final Set<String> ALLOWED_SEARCH_FIELDS = CountrySearchField.allowedFields();

    private final CountryRepository countryRepository;

    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Transactional
    @Override
    public SuccessResponse create(CreateCountryRequest request, Map<Long, LocaleEntity> localeEntityMap) {
        if (countryRepository.existsByCodeAndIsActiveAndIsDeleted(request.getCode(), true, false)) {
            throw new IllegalStateException("Country with code '" + request.getCode() + "' already exists");
        }

        CountryEntity entity = CountryMapper.create(request);

        request.getLocales().forEach(localeReq -> {
            CountryLocaleEntity countryLocaleEntity = CountryLocaleMapper.create(localeReq);

            LocaleEntity localeEntity = localeEntityMap.get(localeReq.getLocaleId());
            localeEntity.addCountryLocaleEntity(countryLocaleEntity);

            entity.addCountryLocaleEntity(countryLocaleEntity);
        });

        countryRepository.save(entity);
        log.info("Country created with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public CountryEntity getEntityById(Long id) {
        return countryRepository.findByIdAndIsActiveAndIsDeleted(id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("Country not found with id: " + id));
    }

    @Override
    public CountryResponse getById(Long id) {
        CountryEntity entity = getEntityById(id);
        CountryDto dto = CountryMapper.toDto(entity);
        return new CountryResponse(dto);
    }

    @Override
    public PaginatedResponse<CountryDto> getAll(CountryFilterRequest request, Long localeId) {
        Page<@NonNull CountryDto> page = countryRepository
                .findAll(CountrySpecification.filter(request, localeId), request.toPageable(ALLOWED_SORT_FIELDS, CountrySortField.localeSortFields()))
                .map(entity -> CountryMapper.toDto(entity, localeId));
        return Pagination.buildPaginatedResponse(page, ALLOWED_SORT_FIELDS, ALLOWED_SEARCH_FIELDS);
    }

    @Transactional
    @Override
    public SuccessResponse update(CountryEntity entity, UpdateCountryRequest request) {
        CountryMapper.update(entity, request);
        countryRepository.save(entity);
        log.info("Country updated with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional
    @Override
    public SuccessResponse delete(CountryEntity entity) {
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        countryRepository.save(entity);
        log.info("Country soft-deleted with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }
}
