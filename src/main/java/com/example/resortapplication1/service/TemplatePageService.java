package com.example.resortapplication1.service;

import com.example.resortapplication1.commons.dto.response.PaginatedResponse;
import com.example.resortapplication1.commons.dto.response.SuccessResponse;
import com.example.resortapplication1.dto.request.templatepages.CreateTemplatePageRequest;
import com.example.resortapplication1.dto.request.templatepages.UpdateTemplatePageRequest;
import com.example.resortapplication1.dto.response.templatepages.TemplatePageResponse;
import com.example.resortapplication1.model.dto.TemplatePageDto;
import com.example.resortapplication1.model.entity.TemplatePageEntity;
import org.springframework.data.domain.Pageable;

public interface TemplatePageService {
    SuccessResponse createTemplatePage(CreateTemplatePageRequest request);

    TemplatePageEntity getTemplatePageById(Long id);

    TemplatePageResponse getTemplatePage(Long id);

    PaginatedResponse<TemplatePageDto> getAllTemplatePages(Pageable pageable);

    TemplatePageResponse updateTemplatePage(Long id, UpdateTemplatePageRequest request);

    SuccessResponse deleteTemplatePage(Long id);
}
