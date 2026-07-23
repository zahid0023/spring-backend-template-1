package com.example.springbackendtemplate1.unit.service;

import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.unit.dto.request.unittype.unittypelocale.CreateUnitTypeLocaleRequest;
import com.example.springbackendtemplate1.unit.dto.request.unittype.unittypelocale.UpdateUnitTypeLocaleRequest;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeLocaleEntity;

public interface UnitTypeLocaleService {

    SuccessResponse create(UnitTypeEntity unitTypeEntity,
                           LocaleEntity localeEntity,
                           CreateUnitTypeLocaleRequest request);

    UnitTypeLocaleEntity getEntityById(Long unitTypeId, Long id);

    SuccessResponse update(UnitTypeLocaleEntity entity,
                           UpdateUnitTypeLocaleRequest request);

    SuccessResponse delete(UnitTypeLocaleEntity entity);
}
