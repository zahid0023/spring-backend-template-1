package com.example.springbackendtemplate1.unit.service;

import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.unit.dto.request.unittype.CreateUnitTypeRequest;
import com.example.springbackendtemplate1.unit.dto.request.unittype.UnitTypeFilterRequest;
import com.example.springbackendtemplate1.unit.dto.request.unittype.UpdateUnitTypeRequest;
import com.example.springbackendtemplate1.unit.dto.response.unittypes.UnitTypeResponse;
import com.example.springbackendtemplate1.unit.model.dto.UnitTypeDto;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;

public interface UnitTypeService {

    SuccessResponse create(CreateUnitTypeRequest request,
                           LocaleEntity localeEntity);

    UnitTypeEntity getEntityById(Long id);

    UnitTypeResponse getById(Long id);

    PaginatedResponse<UnitTypeDto> getAll(UnitTypeFilterRequest request);

    SuccessResponse update(UnitTypeEntity entity,
                           UpdateUnitTypeRequest request);

    SuccessResponse delete(UnitTypeEntity entity);
}
