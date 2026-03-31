package com.example.resortapplication1.service;

import com.example.resortapplication1.commons.dto.response.PaginatedResponse;
import com.example.resortapplication1.commons.dto.response.SuccessResponse;
import com.example.resortapplication1.dto.request.templatepageslots.CreateTemplatePageSlotRequest;
import com.example.resortapplication1.dto.request.templatepageslots.UpdateTemplatePageSlotRequest;
import com.example.resortapplication1.dto.response.templatepageslots.TemplatePageSlotResponse;
import com.example.resortapplication1.model.dto.TemplatePageSlotDto;
import com.example.resortapplication1.model.entity.TemplatePageSlotEntity;
import org.springframework.data.domain.Pageable;

public interface TemplatePageSlotService {
    SuccessResponse createTemplatePageSlot(CreateTemplatePageSlotRequest request);

    TemplatePageSlotEntity getTemplatePageSlotById(Long id);

    TemplatePageSlotResponse getTemplatePageSlot(Long id);

    PaginatedResponse<TemplatePageSlotDto> getAllTemplatePageSlots(Pageable pageable);

    TemplatePageSlotResponse updateTemplatePageSlot(Long id, UpdateTemplatePageSlotRequest request);

    SuccessResponse deleteTemplatePageSlot(Long id);
}
