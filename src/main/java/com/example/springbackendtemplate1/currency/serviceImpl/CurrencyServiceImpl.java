package com.example.springbackendtemplate1.currency.serviceImpl;

import com.example.springbackendtemplate1.address.model.dto.CountryDto;
import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.address.model.mapper.CountryMapper;
import com.example.springbackendtemplate1.commons.context.LocaleContext;
import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.commons.utils.Pagination;
import com.example.springbackendtemplate1.currency.dto.request.currency.CurrencyFilterRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.CreateCurrencyRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.UpdateCurrencyRequest;
import com.example.springbackendtemplate1.currency.dto.response.currencies.CurrencyResponse;
import com.example.springbackendtemplate1.currency.model.dto.CurrencyDto;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyEntity;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyLocaleEntity;
import com.example.springbackendtemplate1.currency.model.enums.CurrencySearchField;
import com.example.springbackendtemplate1.currency.model.enums.CurrencySortField;
import com.example.springbackendtemplate1.currency.model.mapper.CurrencyLocaleMapper;
import com.example.springbackendtemplate1.currency.model.mapper.CurrencyMapper;
import com.example.springbackendtemplate1.currency.repository.CurrencyRepository;
import com.example.springbackendtemplate1.currency.service.CurrencyService;
import com.example.springbackendtemplate1.currency.specification.CurrencySpecification;
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
public class CurrencyServiceImpl implements CurrencyService {

    private static final Set<String> ALLOWED_SORT_FIELDS = CurrencySortField.allowedFields();
    private static final Set<String> ALLOWED_SEARCH_FIELDS = CurrencySearchField.allowedFields();

    private final CurrencyRepository currencyRepository;

    public CurrencyServiceImpl(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Transactional
    @Override
    public SuccessResponse create(CreateCurrencyRequest request, CountryEntity countryEntity, LocaleEntity localeEntity) {
        if (currencyRepository.existsByCodeAndIsActiveAndIsDeleted(request.getCode(), true, false)) {
            throw new IllegalStateException("Currency with code '" + request.getCode() + "' already exists");
        }
        if (currencyRepository.existsByNumericCodeAndIsActiveAndIsDeleted(request.getNumericCode(), true, false)) {
            throw new IllegalStateException("Currency with numeric code '" + request.getNumericCode() + "' already exists");
        }

        CurrencyEntity entity = CurrencyMapper.create(request);
        countryEntity.addCurrencyEntity(entity);

        CurrencyLocaleEntity currencyLocaleEntity = CurrencyLocaleMapper.create(request.getLocale());
        localeEntity.addCurrencyLocaleEntity(currencyLocaleEntity);
        entity.addCurrencyLocaleEntity(currencyLocaleEntity);

        currencyRepository.save(entity);
        log.info("Currency created with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public CurrencyEntity getEntityById(Long id) {
        return currencyRepository.findByIdAndIsActiveAndIsDeleted(id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("Currency not found with id: " + id));
    }

    @Override
    public CurrencyResponse getById(Long id) {
        CurrencyEntity entity = getEntityById(id);
        CountryDto country = CountryMapper.toDto(entity.getCountryEntity(), false).build();
        CurrencyDto dto = CurrencyMapper.toDto(entity, true)
                .country(country)
                .build();
        return new CurrencyResponse(dto);
    }

    @Override
    public PaginatedResponse<CurrencyDto> getAll(CurrencyFilterRequest request) {
        Specification<@NonNull CurrencyEntity> specification = CurrencySpecification.filter(request, LocaleContext.getLocaleId());
        Pageable pageable = request.toPageable(ALLOWED_SORT_FIELDS, CurrencySortField.localeSortFields());
        Page<@NonNull CurrencyDto> page = currencyRepository
                .findAll(specification, pageable)
                .map(entity -> CurrencyMapper.toDto(entity, false)
                        .country(CountryMapper.toDto(entity.getCountryEntity(), false).build())
                        .build());
        return Pagination.buildPaginatedResponse(page, ALLOWED_SORT_FIELDS, ALLOWED_SEARCH_FIELDS);
    }

    @Transactional
    @Override
    public SuccessResponse update(CurrencyEntity entity, UpdateCurrencyRequest request) {
        CurrencyMapper.update(entity, request);
        currencyRepository.save(entity);
        log.info("Currency updated with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional
    @Override
    public SuccessResponse delete(CurrencyEntity entity) {
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        currencyRepository.save(entity);
        log.info("Currency soft-deleted with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }
}
