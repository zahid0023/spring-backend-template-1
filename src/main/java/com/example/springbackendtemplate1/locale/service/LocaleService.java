package com.example.springbackendtemplate1.locale.service;

import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.locale.dto.request.locale.CreateLocaleRequest;
import com.example.springbackendtemplate1.locale.dto.request.locale.LocaleFilterRequest;
import com.example.springbackendtemplate1.locale.dto.request.locale.UpdateLocaleRequest;
import com.example.springbackendtemplate1.locale.dto.response.locales.LocaleResponse;
import com.example.springbackendtemplate1.locale.model.dto.LocaleDto;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;

import java.util.List;
import java.util.Set;

public interface LocaleService {
    SuccessResponse create(CreateLocaleRequest request);

    LocaleEntity getEntityById(Long id);

    LocaleResponse getById(Long id);

    PaginatedResponse<LocaleDto> getAll(LocaleFilterRequest request);

    SuccessResponse update(LocaleEntity entity, UpdateLocaleRequest request);

    SuccessResponse delete(LocaleEntity entity);

    List<LocaleEntity> getAll(Set<Long> ids);
}
