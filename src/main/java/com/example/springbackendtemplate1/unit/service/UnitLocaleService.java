package com.example.springbackendtemplate1.unit.service;

import com.example.springbackendtemplate1.commons.dto.request.PaginatedRequest;
import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.unit.dto.request.unit.locale.CreateUnitLocaleRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.locale.UpdateUnitLocaleRequest;
import com.example.springbackendtemplate1.unit.model.dto.UnitLocaleDto;
import com.example.springbackendtemplate1.unit.model.entity.UnitEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;

public interface UnitLocaleService {
    SuccessResponse create(CreateUnitLocaleRequest request,
                           UnitEntity unitEntity,
                           LocaleEntity localeEntity);

    UnitLocaleEntity getEntityById(Long unitId, Long id);

    PaginatedResponse<UnitLocaleDto> getAll(Long unitId, String localeCode, PaginatedRequest paginatedRequest);

    SuccessResponse update(UnitLocaleEntity entity,
                           UpdateUnitLocaleRequest request);

    SuccessResponse delete(UnitLocaleEntity entity);
}
