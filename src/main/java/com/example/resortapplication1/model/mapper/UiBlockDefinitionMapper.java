package com.example.resortapplication1.model.mapper;

import com.example.resortapplication1.dto.request.uiblockdefinitions.CreateUiBlockDefinitionRequest;
import com.example.resortapplication1.dto.request.uiblockdefinitions.UpdateUiBlockDefinitionRequest;
import com.example.resortapplication1.model.dto.UiBlockDefinitionDto;
import com.example.resortapplication1.model.entity.UiBlockCategoryEntity;
import com.example.resortapplication1.model.entity.UiBlockDefinitionEntity;
import com.example.resortapplication1.model.entity.PageTypeEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UiBlockDefinitionMapper {

    public static UiBlockDefinitionEntity fromRequest(CreateUiBlockDefinitionRequest request,
                                                      UiBlockCategoryEntity category,
                                                      PageTypeEntity pageType) {
        UiBlockDefinitionEntity entity = new UiBlockDefinitionEntity();
        entity.setUiBlockKey(request.getUiBlockKey());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setUiBlockVersion(request.getUiBlockVersion() != null ? request.getUiBlockVersion() : "1.0.0");
        entity.setUiBlockCategoryEntity(category);
        entity.setPageTypeEntity(pageType);
        entity.setEditableSchema(request.getEditableSchema());
        entity.setDefaultContent(request.getDefaultContent());
        entity.setAllowedPages(request.getAllowedPages());
        entity.setStatus(request.getStatus() != null ? request.getStatus() : "draft");
        return entity;
    }

    public static void updateEntity(UiBlockDefinitionEntity entity,
                                    UpdateUiBlockDefinitionRequest request,
                                    UiBlockCategoryEntity category,
                                    PageTypeEntity pageType) {
        if (request.getUiBlockKey() != null) entity.setUiBlockKey(request.getUiBlockKey());
        if (request.getName() != null) entity.setName(request.getName());
        if (request.getDescription() != null) entity.setDescription(request.getDescription());
        if (request.getUiBlockVersion() != null) entity.setUiBlockVersion(request.getUiBlockVersion());
        if (category != null) entity.setUiBlockCategoryEntity(category);
        if (pageType != null) entity.setPageTypeEntity(pageType);
        if (request.getEditableSchema() != null) entity.setEditableSchema(request.getEditableSchema());
        if (request.getDefaultContent() != null) entity.setDefaultContent(request.getDefaultContent());
        if (request.getAllowedPages() != null) entity.setAllowedPages(request.getAllowedPages());
        if (request.getStatus() != null) entity.setStatus(request.getStatus());
    }

    public static UiBlockDefinitionDto toDto(UiBlockDefinitionEntity entity) {
        return UiBlockDefinitionDto.builder()
                .id(entity.getId())
                .uiBlockKey(entity.getUiBlockKey())
                .name(entity.getName())
                .description(entity.getDescription())
                .uiBlockVersion(entity.getUiBlockVersion())
                .uiBlockCategoryId(entity.getUiBlockCategoryEntity().getId())
                .pageTypeId(entity.getPageTypeEntity().getId())
                .editableSchema(entity.getEditableSchema())
                .defaultContent(entity.getDefaultContent())
                .allowedPages(entity.getAllowedPages())
                .status(entity.getStatus())
                .build();
    }
}
