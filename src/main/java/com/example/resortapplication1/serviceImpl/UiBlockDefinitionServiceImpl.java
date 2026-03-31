package com.example.resortapplication1.serviceImpl;

import com.example.resortapplication1.commons.dto.response.PaginatedResponse;
import com.example.resortapplication1.commons.dto.response.SuccessResponse;
import com.example.resortapplication1.dto.request.uiblockdefinitions.CreateUiBlockDefinitionRequest;
import com.example.resortapplication1.dto.request.uiblockdefinitions.UpdateUiBlockDefinitionRequest;
import com.example.resortapplication1.dto.response.uiblockdefinitions.UiBlockDefinitionResponse;
import com.example.resortapplication1.model.dto.UiBlockDefinitionDto;
import com.example.resortapplication1.model.entity.UiBlockCategoryEntity;
import com.example.resortapplication1.model.entity.UiBlockDefinitionEntity;
import com.example.resortapplication1.model.entity.PageTypeEntity;
import com.example.resortapplication1.model.mapper.UiBlockDefinitionMapper;
import com.example.resortapplication1.repository.UiBlockDefinitionRepository;
import com.example.resortapplication1.service.PageTypeService;
import com.example.resortapplication1.service.UiBlockCategoryService;
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
public class UiBlockDefinitionServiceImpl implements UiBlockDefinitionService {

    private final UiBlockDefinitionRepository uiBlockDefinitionRepository;
    private final UiBlockCategoryService uiBlockCategoryService;
    private final PageTypeService pageTypeService;

    public UiBlockDefinitionServiceImpl(UiBlockDefinitionRepository uiBlockDefinitionRepository,
                                        UiBlockCategoryService uiBlockCategoryService,
                                        PageTypeService pageTypeService) {
        this.uiBlockDefinitionRepository = uiBlockDefinitionRepository;
        this.uiBlockCategoryService = uiBlockCategoryService;
        this.pageTypeService = pageTypeService;
    }

    @Override
    public SuccessResponse createUiBlockDefinition(CreateUiBlockDefinitionRequest request) {
        UiBlockCategoryEntity category = uiBlockCategoryService.getUiBlockCategoryById(request.getUiBlockCategoryId());
        PageTypeEntity pageType = pageTypeService.getPageTypeById(request.getPageTypeId());
        UiBlockDefinitionEntity entity = UiBlockDefinitionMapper.fromRequest(request, category, pageType);
        entity = uiBlockDefinitionRepository.save(entity);
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public UiBlockDefinitionResponse getUiBlockDefinition(Long id) {
        UiBlockDefinitionEntity entity = getUiBlockDefinitionById(id);
        UiBlockDefinitionDto dto = UiBlockDefinitionMapper.toDto(entity);
        return new UiBlockDefinitionResponse(dto);
    }

    @Override
    public PaginatedResponse<UiBlockDefinitionDto> getAllUiBlockDefinitions(Pageable pageable) {
        Page<@NonNull UiBlockDefinitionEntity> page = uiBlockDefinitionRepository.findAllByIsActiveAndIsDeleted(true, false, pageable);
        Page<@NonNull UiBlockDefinitionDto> dtoPage = page.map(UiBlockDefinitionMapper::toDto);
        return Pagination.buildPaginatedResponse(dtoPage);
    }

    @Override
    public UiBlockDefinitionResponse updateUiBlockDefinition(Long id, UpdateUiBlockDefinitionRequest request) {
        UiBlockDefinitionEntity entity = getUiBlockDefinitionById(id);

        UiBlockCategoryEntity category = request.getUiBlockCategoryId() != null
                ? uiBlockCategoryService.getUiBlockCategoryById(request.getUiBlockCategoryId())
                : null;
        PageTypeEntity pageType = request.getPageTypeId() != null
                ? pageTypeService.getPageTypeById(request.getPageTypeId())
                : null;

        UiBlockDefinitionMapper.updateEntity(entity, request, category, pageType);
        entity = uiBlockDefinitionRepository.save(entity);
        UiBlockDefinitionDto dto = UiBlockDefinitionMapper.toDto(entity);
        return new UiBlockDefinitionResponse(dto);
    }

    @Override
    public SuccessResponse deleteUiBlockDefinition(Long id) {
        UiBlockDefinitionEntity entity = getUiBlockDefinitionById(id);
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        uiBlockDefinitionRepository.save(entity);
        return new SuccessResponse(true, id);
    }

    @Override
    public UiBlockDefinitionEntity getUiBlockDefinitionById(Long id) {
        return uiBlockDefinitionRepository.findByIdAndIsActiveAndIsDeleted(id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("UI Block Definition with id: " + id + " was not found."));
    }
}
