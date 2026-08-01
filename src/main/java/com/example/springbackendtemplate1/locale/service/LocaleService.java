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

    LocaleEntity getEntityByCode(String code);

    LocaleResponse getById(Long id);

    PaginatedResponse<LocaleDto> getAll(LocaleFilterRequest request);

    SuccessResponse update(LocaleEntity entity, UpdateLocaleRequest request);

    SuccessResponse delete(LocaleEntity entity);

    List<LocaleEntity> getAll(Set<Long> ids);

    /**
     * Resolves the LocaleEntity id matching the caller's Accept-Language header.
     * <p>
     * Parses the primary language tag from the header (e.g. "en-US,en;q=0.9" -> "en";
     * a plain "en" stays "en"; a null/blank header is treated as no preference and
     * falls straight through to the "en" fallback below).
     * <p>
     * Looks up {@code LocaleRepository.findByCodeAndIsActiveAndIsDeleted(code, true, false)}.
     * If a match is found, returns its id. If no match is found and the parsed code was
     * not already "en", retries the same lookup with code "en". If still no match is
     * found, returns null.
     *
     * @param acceptLanguageHeader raw value of the Accept-Language request header, may be null/blank
     * @return the resolved LocaleEntity id, or null if neither the requested language nor "en" exists
     */
    Long resolveLocaleId(String acceptLanguageHeader);
}
