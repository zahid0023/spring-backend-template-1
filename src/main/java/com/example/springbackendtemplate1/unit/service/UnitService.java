package com.example.springbackendtemplate1.unit.service;

import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.unit.dto.request.unit.CreateUnitRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.UnitFilterRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.UpdateUnitRequest;
import com.example.springbackendtemplate1.unit.dto.response.units.UnitResponse;
import com.example.springbackendtemplate1.unit.model.dto.UnitDto;
import com.example.springbackendtemplate1.unit.model.entity.UnitEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;

public interface UnitService {

    SuccessResponse create(CreateUnitRequest request,
                           UnitTypeEntity unitTypeEntity,
                           LocaleEntity localeEntity);

    UnitEntity getEntityById(Long id);

    UnitResponse getById(Long id);

    PaginatedResponse<UnitDto> getAll(UnitFilterRequest request);

    SuccessResponse update(UnitEntity entity,
                           UpdateUnitRequest request);

    SuccessResponse delete(UnitEntity entity);
}
