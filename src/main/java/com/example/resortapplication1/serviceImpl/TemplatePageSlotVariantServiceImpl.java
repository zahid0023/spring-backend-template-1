package com.example.resortapplication1.serviceImpl;

import com.example.resortapplication1.commons.dto.response.PaginatedResponse;
import com.example.resortapplication1.commons.dto.response.SuccessResponse;
import com.example.resortapplication1.dto.request.templatepageslotvariants.CreateTemplatePageSlotVariantRequest;
import com.example.resortapplication1.dto.request.templatepageslotvariants.UpdateTemplatePageSlotVariantRequest;
import com.example.resortapplication1.dto.response.templatepageslotvariants.TemplatePageSlotVariantResponse;
import com.example.resortapplication1.model.dto.TemplatePageSlotVariantDto;
import com.example.resortapplication1.model.entity.TemplatePageSlotEntity;
import com.example.resortapplication1.model.entity.TemplatePageSlotVariantEntity;
import com.example.resortapplication1.model.entity.UiBlockDefinitionEntity;
import com.example.resortapplication1.model.mapper.TemplatePageSlotVariantMapper;
import com.example.resortapplication1.repository.TemplatePageSlotVariantRepository;
import com.example.resortapplication1.service.TemplatePageSlotService;
import com.example.resortapplication1.service.TemplatePageSlotVariantService;
import com.example.resortapplication1.service.UiBlockDefinitionService;
import com.example.resortapplication1.utils.Pagination;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TemplatePageSlotVariantServiceImpl implements TemplatePageSlotVariantService {

    private final TemplatePageSlotVariantRepository templatePageSlotVariantRepository;
    private final TemplatePageSlotService templatePageSlotService;
    private final UiBlockDefinitionService uiBlockDefinitionService;

    public TemplatePageSlotVariantServiceImpl(TemplatePageSlotVariantRepository templatePageSlotVariantRepository,
                                              TemplatePageSlotService templatePageSlotService,
                                              UiBlockDefinitionService uiBlockDefinitionService) {
        this.templatePageSlotVariantRepository = templatePageSlotVariantRepository;
        this.templatePageSlotService = templatePageSlotService;
        this.uiBlockDefinitionService = uiBlockDefinitionService;
    }

    @Override
    public SuccessResponse createTemplatePageSlotVariant(CreateTemplatePageSlotVariantRequest request) {
        TemplatePageSlotEntity templatePageSlot = templatePageSlotService.getTemplatePageSlotById(request.getTemplatePageSlotId());
        UiBlockDefinitionEntity uiBlockDefinition = uiBlockDefinitionService.getUiBlockDefinitionById(request.getUiBlockDefinitionId());
        TemplatePageSlotVariantEntity entity = TemplatePageSlotVariantMapper.fromRequest(request, templatePageSlot, uiBlockDefinition);
        entity = templatePageSlotVariantRepository.save(entity);
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public TemplatePageSlotVariantEntity getTemplatePageSlotVariantById(Long id) {
        return templatePageSlotVariantRepository.findByIdAndIsActiveAndIsDeleted(id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("Template Page Slot Variant with id: " + id + " was not found."));
    }

    @Override
    public TemplatePageSlotVariantResponse getTemplatePageSlotVariant(Long id) {
        TemplatePageSlotVariantEntity entity = getTemplatePageSlotVariantById(id);
        TemplatePageSlotVariantDto dto = TemplatePageSlotVariantMapper.toDto(entity);
        return new TemplatePageSlotVariantResponse(dto);
    }

    @Override
    public PaginatedResponse<TemplatePageSlotVariantDto> getAllTemplatePageSlotVariants(Pageable pageable) {
        Page<@NonNull TemplatePageSlotVariantEntity> page = templatePageSlotVariantRepository.findAllByIsActiveAndIsDeleted(true, false, pageable);
        Page<@NonNull TemplatePageSlotVariantDto> dtoPage = page.map(TemplatePageSlotVariantMapper::toDto);
        return Pagination.buildPaginatedResponse(dtoPage);
    }

    @Override
    public TemplatePageSlotVariantResponse updateTemplatePageSlotVariant(Long id, UpdateTemplatePageSlotVariantRequest request) {
        TemplatePageSlotVariantEntity entity = getTemplatePageSlotVariantById(id);

        TemplatePageSlotEntity templatePageSlot = request.getTemplatePageSlotId() != null
                ? templatePageSlotService.getTemplatePageSlotById(request.getTemplatePageSlotId())
                : null;
        UiBlockDefinitionEntity uiBlockDefinition = request.getUiBlockDefinitionId() != null
                ? uiBlockDefinitionService.getUiBlockDefinitionById(request.getUiBlockDefinitionId())
                : null;

        TemplatePageSlotVariantMapper.updateEntity(entity, request, templatePageSlot, uiBlockDefinition);
        entity = templatePageSlotVariantRepository.save(entity);
        TemplatePageSlotVariantDto dto = TemplatePageSlotVariantMapper.toDto(entity);
        return new TemplatePageSlotVariantResponse(dto);
    }

    @Override
    public SuccessResponse deleteTemplatePageSlotVariant(Long id) {
        TemplatePageSlotVariantEntity entity = getTemplatePageSlotVariantById(id);
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        templatePageSlotVariantRepository.save(entity);
        return new SuccessResponse(true, id);
    }
}
