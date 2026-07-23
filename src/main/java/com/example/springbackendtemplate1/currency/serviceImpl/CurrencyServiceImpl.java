package com.example.springbackendtemplate1.currency.serviceImpl;

import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.commons.utils.EntityValidator;
import com.example.springbackendtemplate1.commons.utils.Pagination;
import com.example.springbackendtemplate1.currency.dto.request.currency.CreateCurrencyRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.CurrencyFilterRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.UpdateCurrencyRequest;
import com.example.springbackendtemplate1.currency.dto.response.currencies.CurrencyResponse;
import com.example.springbackendtemplate1.currency.model.dto.CurrencyDto;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyEntity;
import com.example.springbackendtemplate1.currency.model.enums.CurrencySearchField;
import com.example.springbackendtemplate1.currency.model.enums.CurrencySortField;
import com.example.springbackendtemplate1.currency.model.mapper.CurrencyMapper;
import com.example.springbackendtemplate1.currency.repository.CurrencyRepository;
import com.example.springbackendtemplate1.currency.service.CurrencyService;
import com.example.springbackendtemplate1.currency.specification.CurrencySpecification;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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
    public SuccessResponse create(CreateCurrencyRequest request,
                                  CountryEntity countryEntity,
                                  Map<Long, LocaleEntity> localeEntityMap) {
        CurrencyEntity entity = CurrencyMapper.create(request, countryEntity, localeEntityMap);
        currencyRepository.save(entity);
        log.info("Currency created with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public CurrencyEntity getEntityById(Long id) {
        return currencyRepository.findByIdAndIsActiveAndIsDeleted(id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("Currency not found with id: " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public CurrencyResponse getById(Long id) {
        return new CurrencyResponse(CurrencyMapper.toDto(getEntityById(id)));
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedResponse<CurrencyDto> getAll(CurrencyFilterRequest request) {
        Page<@NonNull CurrencyDto> page = currencyRepository
                .findAll(CurrencySpecification.filter(request), request.toPageable(ALLOWED_SORT_FIELDS))
                .map(CurrencyMapper::toDto);
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
    public SuccessResponse delete(Long id) {
        CurrencyEntity entity = getEntityById(id);
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        currencyRepository.save(entity);
        log.info("Currency soft-deleted with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<CurrencyEntity> getAll(Set<Long> ids) {
        List<CurrencyEntity> entities = currencyRepository.findAllByIdInAndIsActiveAndIsDeleted(ids, true, false);
        EntityValidator.validateAllFound(ids, entities, CurrencyEntity::getId, "Currency");
        return entities;
    }
}
