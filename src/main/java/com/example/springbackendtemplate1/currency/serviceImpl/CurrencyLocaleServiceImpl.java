package com.example.springbackendtemplate1.currency.serviceImpl;

import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.currency.dto.request.currency.currencylocale.CreateCurrencyLocaleRequest;
import com.example.springbackendtemplate1.currency.dto.request.currency.currencylocale.UpdateCurrencyLocaleRequest;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyEntity;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyLocaleEntity;
import com.example.springbackendtemplate1.currency.model.mapper.CurrencyLocaleMapper;
import com.example.springbackendtemplate1.currency.repository.CurrencyLocaleRepository;
import com.example.springbackendtemplate1.currency.service.CurrencyLocaleService;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class CurrencyLocaleServiceImpl implements CurrencyLocaleService {

    private final CurrencyLocaleRepository currencyLocaleRepository;

    public CurrencyLocaleServiceImpl(CurrencyLocaleRepository currencyLocaleRepository) {
        this.currencyLocaleRepository = currencyLocaleRepository;
    }

    @Transactional
    @Override
    public SuccessResponse create(CurrencyEntity currencyEntity,
                                  LocaleEntity localeEntity,
                                  CreateCurrencyLocaleRequest request) {
        CurrencyLocaleEntity entity = CurrencyLocaleMapper.create(request, currencyEntity, localeEntity);
        currencyLocaleRepository.save(entity);
        log.info("CurrencyLocale created with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public CurrencyLocaleEntity getEntityById(Long currencyId, Long id) {
        return currencyLocaleRepository
                .findByCurrencyEntity_IdAndIdAndIsActiveAndIsDeleted(currencyId, id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("CurrencyLocale not found with id: " + id));
    }

    @Transactional
    @Override
    public SuccessResponse update(CurrencyLocaleEntity entity, UpdateCurrencyLocaleRequest request) {
        CurrencyLocaleMapper.update(entity, request);
        currencyLocaleRepository.save(entity);
        log.info("CurrencyLocale updated with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }

    @Transactional
    @Override
    public SuccessResponse delete(CurrencyLocaleEntity entity) {
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        currencyLocaleRepository.save(entity);
        log.info("CurrencyLocale soft-deleted with id: {}", entity.getId());
        return new SuccessResponse(true, entity.getId());
    }
}
