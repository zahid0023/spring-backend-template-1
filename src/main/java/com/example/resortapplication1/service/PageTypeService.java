package com.example.resortapplication1.service;

import com.example.resortapplication1.commons.dto.response.PaginatedResponse;
import com.example.resortapplication1.commons.dto.response.SuccessResponse;
import com.example.resortapplication1.dto.request.pagetypes.CreatePageTypeRequest;
import com.example.resortapplication1.dto.request.pagetypes.UpdatePageTypeRequest;
import com.example.resortapplication1.dto.response.pagetypes.PageTypeResponse;
import com.example.resortapplication1.model.dto.PageTypeDto;
import com.example.resortapplication1.model.entity.PageTypeEntity;
import org.springframework.data.domain.Pageable;

public interface PageTypeService {
    SuccessResponse createPageType(CreatePageTypeRequest request);

    PageTypeEntity getPageTypeById(Long id);

    PageTypeResponse getPageType(Long id);

    PaginatedResponse<PageTypeDto> getAllPageTypes(Pageable pageable);

    PageTypeResponse updatePageType(Long id, UpdatePageTypeRequest request);

    SuccessResponse deletePageType(Long id);
}
