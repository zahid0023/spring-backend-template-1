package com.example.resortapplication1.service;

import com.example.resortapplication1.commons.dto.response.PaginatedResponse;
import com.example.resortapplication1.commons.dto.response.SuccessResponse;
import com.example.resortapplication1.dto.request.templates.CreateTemplateRequest;
import com.example.resortapplication1.dto.request.templates.UpdateTemplateRequest;
import com.example.resortapplication1.dto.response.templates.TemplateResponse;
import com.example.resortapplication1.model.dto.TemplateDto;
import com.example.resortapplication1.model.entity.TemplateEntity;
import org.springframework.data.domain.Pageable;

public interface TemplateService {
    SuccessResponse createTemplate(CreateTemplateRequest request);

    TemplateEntity getTemplateById(Long id);

    TemplateResponse getTemplate(Long id);

    PaginatedResponse<TemplateDto> getAllTemplates(Pageable pageable);

    TemplateResponse updateTemplate(Long id, UpdateTemplateRequest request);

    SuccessResponse deleteTemplate(Long id);
}
