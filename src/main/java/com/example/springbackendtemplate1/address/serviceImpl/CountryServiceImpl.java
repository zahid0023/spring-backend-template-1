package com.example.springbackendtemplate1.address.serviceImpl;

import com.example.springbackendtemplate1.address.model.entity.CountryLocaleEntity;
import com.example.springbackendtemplate1.address.model.mapper.CountryLocaleMapper;
import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.commons.utils.EntityValidator;
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

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Service
@Slf4j
public class CountryServiceImpl implements CountryService {

    private static final Set<String> ALLOWED_SORT_FIELDS   = CountrySortField.allowedFields();
    private static final Set<String> ALLOWED_SEARCH_FIELDS = CountrySearchField.allowedFields();

    private final CountryRepository countryRepository;

    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Transactional
    @Override
    public SuccessResponse create(CreateCountryRequest request, List<LocaleEntity> localeEntities) {
        CountryEntity entity = CountryMapper.create(request);
        IntStream.range(0, request.getLocales().size()).forEach(i -> {
            CountryLocaleEntity countryLocaleEntity = CountryLocaleMapper.create(request.getLocales().get(i));
            countryLocaleEntity.assignLocale(localeEntities.get(i));
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
    public PaginatedResponse<CountryDto> getAll(CountryFilterRequest request) {
        Page<@NonNull CountryDto> page = countryRepository
                .findAll(CountrySpecification.filter(request), request.toPageable(ALLOWED_SORT_FIELDS))
                .map(CountryMapper::toDto);
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

    @Override
    public List<CountryEntity> getAll(Set<Long> ids) {
        List<CountryEntity> entities = countryRepository
                .findAllByIdInAndIsActiveAndIsDeleted(ids, true, false);
        EntityValidator.validateAllFound(ids, entities, CountryEntity::getId, "Country");
        return entities;
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
