package com.example.resortapplication1.service;

import com.example.resortapplication1.commons.dto.response.PaginatedResponse;
import com.example.resortapplication1.commons.dto.response.SuccessResponse;
import com.example.resortapplication1.dto.request.templatepageslotvariants.CreateTemplatePageSlotVariantRequest;
import com.example.resortapplication1.dto.request.templatepageslotvariants.UpdateTemplatePageSlotVariantRequest;
import com.example.resortapplication1.dto.response.templatepageslotvariants.TemplatePageSlotVariantResponse;
import com.example.resortapplication1.model.dto.TemplatePageSlotVariantDto;
import com.example.resortapplication1.model.entity.TemplatePageSlotVariantEntity;
import org.springframework.data.domain.Pageable;

public interface TemplatePageSlotVariantService {
    SuccessResponse createTemplatePageSlotVariant(CreateTemplatePageSlotVariantRequest request);

    TemplatePageSlotVariantEntity getTemplatePageSlotVariantById(Long id);

    TemplatePageSlotVariantResponse getTemplatePageSlotVariant(Long id);

    PaginatedResponse<TemplatePageSlotVariantDto> getAllTemplatePageSlotVariants(Pageable pageable);

    TemplatePageSlotVariantResponse updateTemplatePageSlotVariant(Long id, UpdateTemplatePageSlotVariantRequest request);

    SuccessResponse deleteTemplatePageSlotVariant(Long id);
}
