package com.example.springbackendtemplate1.unit.service;

import com.example.springbackendtemplate1.unit.dto.request.unit.CreateUnitRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.UnitFilterRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.UpdateUnitRequest;
import com.example.springbackendtemplate1.unit.dto.response.units.UnitResponse;
import com.example.springbackendtemplate1.unit.model.dto.UnitDto;
import com.example.springbackendtemplate1.unit.model.entity.UnitEntity;
import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UnitService {

    SuccessResponse create(CreateUnitRequest request,
                           UnitTypeEntity unitTypeEntity,
                           Map<Long, LocaleEntity> localeEntityMap);

    UnitEntity getEntityById(Long id);

    UnitResponse getById(Long id);

    PaginatedResponse<UnitDto> getAll(UnitFilterRequest request, Long unitTypeId);

    SuccessResponse update(UnitEntity entity, UpdateUnitRequest request);

    SuccessResponse delete(Long id);

    List<UnitEntity> getAll(Set<Long> ids);
}
