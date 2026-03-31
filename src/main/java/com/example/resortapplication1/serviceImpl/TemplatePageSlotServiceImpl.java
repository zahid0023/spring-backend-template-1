package com.example.resortapplication1.serviceImpl;

import com.example.resortapplication1.commons.dto.response.PaginatedResponse;
import com.example.resortapplication1.commons.dto.response.SuccessResponse;
import com.example.resortapplication1.dto.request.templatepageslots.CreateTemplatePageSlotRequest;
import com.example.resortapplication1.dto.request.templatepageslots.UpdateTemplatePageSlotRequest;
import com.example.resortapplication1.dto.response.templatepageslots.TemplatePageSlotResponse;
import com.example.resortapplication1.model.dto.TemplatePageSlotDto;
import com.example.resortapplication1.model.entity.TemplatePageEntity;
import com.example.resortapplication1.model.entity.TemplatePageSlotEntity;
import com.example.resortapplication1.model.entity.UiBlockCategoryEntity;
import com.example.resortapplication1.model.mapper.TemplatePageSlotMapper;
import com.example.resortapplication1.repository.TemplatePageSlotRepository;
import com.example.resortapplication1.service.TemplatePageService;
import com.example.resortapplication1.service.TemplatePageSlotService;
import com.example.resortapplication1.service.UiBlockCategoryService;
import com.example.resortapplication1.utils.Pagination;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TemplatePageSlotServiceImpl implements TemplatePageSlotService {

    private final TemplatePageSlotRepository templatePageSlotRepository;
    private final TemplatePageService templatePageService;
    private final UiBlockCategoryService uiBlockCategoryService;

    public TemplatePageSlotServiceImpl(TemplatePageSlotRepository templatePageSlotRepository,
                                       TemplatePageService templatePageService,
                                       UiBlockCategoryService uiBlockCategoryService) {
        this.templatePageSlotRepository = templatePageSlotRepository;
        this.templatePageService = templatePageService;
        this.uiBlockCategoryService = uiBlockCategoryService;
    }

    @Override
    public SuccessResponse createTemplatePageSlot(CreateTemplatePageSlotRequest request) {
        TemplatePageEntity templatePage = templatePageService.getTemplatePageById(request.getTemplatePageId());
        UiBlockCategoryEntity uiBlockCategory = uiBlockCategoryService.getUiBlockCategoryById(request.getUiBlockCategoryId());
        TemplatePageSlotEntity entity = TemplatePageSlotMapper.fromRequest(request, templatePage, uiBlockCategory);
        entity = templatePageSlotRepository.save(entity);
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public TemplatePageSlotEntity getTemplatePageSlotById(Long id) {
        return templatePageSlotRepository.findByIdAndIsActiveAndIsDeleted(id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("Template Page Slot with id: " + id + " was not found."));
    }

    @Override
    public TemplatePageSlotResponse getTemplatePageSlot(Long id) {
        TemplatePageSlotEntity entity = getTemplatePageSlotById(id);
        TemplatePageSlotDto dto = TemplatePageSlotMapper.toDto(entity);
        return new TemplatePageSlotResponse(dto);
    }

    @Override
    public PaginatedResponse<TemplatePageSlotDto> getAllTemplatePageSlots(Pageable pageable) {
        Page<@NonNull TemplatePageSlotEntity> page = templatePageSlotRepository.findAllByIsActiveAndIsDeleted(true, false, pageable);
        Page<@NonNull TemplatePageSlotDto> dtoPage = page.map(TemplatePageSlotMapper::toDto);
        return Pagination.buildPaginatedResponse(dtoPage);
    }

    @Override
    public TemplatePageSlotResponse updateTemplatePageSlot(Long id, UpdateTemplatePageSlotRequest request) {
        TemplatePageSlotEntity entity = getTemplatePageSlotById(id);

        TemplatePageEntity templatePage = request.getTemplatePageId() != null
                ? templatePageService.getTemplatePageById(request.getTemplatePageId())
                : null;
        UiBlockCategoryEntity uiBlockCategory = request.getUiBlockCategoryId() != null
                ? uiBlockCategoryService.getUiBlockCategoryById(request.getUiBlockCategoryId())
                : null;

        TemplatePageSlotMapper.updateEntity(entity, request, templatePage, uiBlockCategory);
        entity = templatePageSlotRepository.save(entity);
        TemplatePageSlotDto dto = TemplatePageSlotMapper.toDto(entity);
        return new TemplatePageSlotResponse(dto);
    }

    @Override
    public SuccessResponse deleteTemplatePageSlot(Long id) {
        TemplatePageSlotEntity entity = getTemplatePageSlotById(id);
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        templatePageSlotRepository.save(entity);
        return new SuccessResponse(true, id);
    }
}
