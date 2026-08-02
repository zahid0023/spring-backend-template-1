package com.example.springbackendtemplate1.address.serviceImpl;

import com.example.springbackendtemplate1.commons.dto.request.PaginatedRequest;
import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.commons.utils.Pagination;
import com.example.springbackendtemplate1.address.dto.request.country.locale.CreateCountryLocaleRequest;
import com.example.springbackendtemplate1.address.dto.request.country.locale.UpdateCountryLocaleRequest;
import com.example.springbackendtemplate1.address.model.dto.CountryLocaleDto;
import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.address.model.entity.CountryLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.address.model.mapper.CountryLocaleMapper;
import com.example.springbackendtemplate1.address.repository.CountryLocaleRepository;
import com.example.springbackendtemplate1.address.service.CountryLocaleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Slf4j
public class CountryLocaleServiceImpl implements CountryLocaleService {
    private final CountryLocaleRepository countryLocaleRepository;

    public CountryLocaleServiceImpl(CountryLocaleRepository countryLocaleRepository) {
        this.countryLocaleRepository = countryLocaleRepository;
    }

    @Transactional
    @Override
    public SuccessResponse create(CreateCountryLocaleRequest request,
                                  CountryEntity countryEntity,
                                  LocaleEntity localeEntity) {
        if (countryLocaleRepository.existsByCountryEntity_IdAndLocaleEntity_IdAndIsActiveAndIsDeleted(
                countryEntity.getId(), localeEntity.getId(), true, false)) {
            throw new IllegalStateException("Country already has a locale entry for locale id: " + localeEntity.getId());
        }

        CountryLocaleEntity entity = CountryLocaleMapper.create(request);
        countryEntity.addCountryLocaleEntity(entity);
        localeEntity.addCountryLocaleEntity(entity);
        countryLocaleRepository.save(entity);
        log.info("CountryLocale created with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional
    @Override
    public SuccessResponse update(CountryLocaleEntity entity,
                                  UpdateCountryLocaleRequest request) {
        CountryLocaleMapper.update(entity, request);
        countryLocaleRepository.save(entity);
        log.info("CountryLocale updated with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional
    @Override
    public SuccessResponse delete(CountryLocaleEntity entity) {
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        countryLocaleRepository.save(entity);
        log.info("CountryLocale soft-deleted with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public CountryLocaleEntity getEntityById(Long countryId, Long id) {
        return countryLocaleRepository
                .findByCountryEntity_IdAndIdAndIsActiveAndIsDeleted(countryId, id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("CountryLocale not found with id: " + id));
    }

    @Override
    public PaginatedResponse<CountryLocaleDto> getAll(Long countryId, String localeCode, PaginatedRequest paginatedRequest) {
        Pageable pageable = paginatedRequest.toPageable(Set.of());
        Page<@NonNull CountryLocaleDto> dtoPage = (localeCode == null || localeCode.isBlank()
                ? countryLocaleRepository.findByCountryEntity_IdAndIsActiveAndIsDeleted(countryId, true, false, pageable)
                : countryLocaleRepository.findByCountryEntity_IdAndLocaleEntity_CodeContainingIgnoreCaseAndIsActiveAndIsDeleted(
                        countryId, localeCode, true, false, pageable))
                .map(CountryLocaleMapper::toDto);
        return Pagination.buildPaginatedResponse(dtoPage);
    }
}
