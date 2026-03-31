package com.example.resortapplication1.model.mapper;

import com.example.resortapplication1.dto.request.templatepageslots.CreateTemplatePageSlotRequest;
import com.example.resortapplication1.dto.request.templatepageslots.UpdateTemplatePageSlotRequest;
import com.example.resortapplication1.model.dto.TemplatePageSlotDto;
import com.example.resortapplication1.model.entity.TemplatePageEntity;
import com.example.resortapplication1.model.entity.TemplatePageSlotEntity;
import com.example.resortapplication1.model.entity.UiBlockCategoryEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TemplatePageSlotMapper {

    public static TemplatePageSlotEntity fromRequest(CreateTemplatePageSlotRequest request,
                                                     TemplatePageEntity templatePage,
                                                     UiBlockCategoryEntity uiBlockCategory) {
        TemplatePageSlotEntity entity = new TemplatePageSlotEntity();
        entity.setTemplatePageEntity(templatePage);
        entity.setUiBlockCategoryEntity(uiBlockCategory);
        entity.setSlotName(request.getSlotName());
        entity.setIsRequired(request.getIsRequired() != null ? request.getIsRequired() : false);
        entity.setSlotOrder(request.getSlotOrder());
        return entity;
    }

    public static void updateEntity(TemplatePageSlotEntity entity,
                                    UpdateTemplatePageSlotRequest request,
                                    TemplatePageEntity templatePage,
                                    UiBlockCategoryEntity uiBlockCategory) {
        if (templatePage != null) entity.setTemplatePageEntity(templatePage);
        if (uiBlockCategory != null) entity.setUiBlockCategoryEntity(uiBlockCategory);
        if (request.getSlotName() != null) entity.setSlotName(request.getSlotName());
        if (request.getIsRequired() != null) entity.setIsRequired(request.getIsRequired());
        if (request.getSlotOrder() != null) entity.setSlotOrder(request.getSlotOrder());
    }

    public static TemplatePageSlotDto toDto(TemplatePageSlotEntity entity) {
        return TemplatePageSlotDto.builder()
                .id(entity.getId())
                .templatePageId(entity.getTemplatePageEntity().getId())
                .uiBlockCategoryId(entity.getUiBlockCategoryEntity().getId())
                .slotName(entity.getSlotName())
                .isRequired(entity.getIsRequired())
                .slotOrder(entity.getSlotOrder())
                .templatePageSlotVariants(entity.getTemplatePageSlotVariantEntities().stream()
                        .map(TemplatePageSlotVariantMapper::toDto)
                        .toList())
                .build();
    }
}
