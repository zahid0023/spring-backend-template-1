package com.example.springbackendtemplate1.unit.service;

import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.unit.dto.request.unittype.locale.CreateUnitTypeLocaleRequest;
import com.example.springbackendtemplate1.unit.dto.request.unittype.locale.UpdateUnitTypeLocaleRequest;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;

public interface UnitTypeLocaleService {
    SuccessResponse create(CreateUnitTypeLocaleRequest request,
                           UnitTypeEntity unitTypeEntity,
                           LocaleEntity localeEntity);

    UnitTypeLocaleEntity getEntityById(Long unitTypeId, Long id);

    SuccessResponse update(UnitTypeLocaleEntity entity,
                           UpdateUnitTypeLocaleRequest request);

    SuccessResponse delete(UnitTypeLocaleEntity entity);
}
