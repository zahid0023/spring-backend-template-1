package com.example.resortapplication1.model.mapper;

import com.example.resortapplication1.dto.request.templatepageslotvariants.CreateTemplatePageSlotVariantRequest;
import com.example.resortapplication1.dto.request.templatepageslotvariants.UpdateTemplatePageSlotVariantRequest;
import com.example.resortapplication1.model.dto.TemplatePageSlotVariantDto;
import com.example.resortapplication1.model.entity.TemplatePageSlotEntity;
import com.example.resortapplication1.model.entity.TemplatePageSlotVariantEntity;
import com.example.resortapplication1.model.entity.UiBlockDefinitionEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TemplatePageSlotVariantMapper {

    public static TemplatePageSlotVariantEntity fromRequest(CreateTemplatePageSlotVariantRequest request,
                                                            TemplatePageSlotEntity templatePageSlot,
                                                            UiBlockDefinitionEntity uiBlockDefinition) {
        TemplatePageSlotVariantEntity entity = new TemplatePageSlotVariantEntity();
        entity.setTemplatePageSlotEntity(templatePageSlot);
        entity.setUiBlockDefinitionEntity(uiBlockDefinition);
        entity.setDisplayOrder(request.getDisplayOrder());
        entity.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : false);
        return entity;
    }

    public static void updateEntity(TemplatePageSlotVariantEntity entity,
                                    UpdateTemplatePageSlotVariantRequest request,
                                    TemplatePageSlotEntity templatePageSlot,
                                    UiBlockDefinitionEntity uiBlockDefinition) {
        if (templatePageSlot != null) entity.setTemplatePageSlotEntity(templatePageSlot);
        if (uiBlockDefinition != null) entity.setUiBlockDefinitionEntity(uiBlockDefinition);
        if (request.getDisplayOrder() != null) entity.setDisplayOrder(request.getDisplayOrder());
        if (request.getIsDefault() != null) entity.setIsDefault(request.getIsDefault());
    }

    public static TemplatePageSlotVariantDto toDto(TemplatePageSlotVariantEntity entity) {
        return TemplatePageSlotVariantDto.builder()
                .id(entity.getId())
                .templatePageSlotId(entity.getTemplatePageSlotEntity().getId())
                .uiBlockDefinitionId(entity.getUiBlockDefinitionEntity().getId())
                .displayOrder(entity.getDisplayOrder())
                .isDefault(entity.getIsDefault())
                .build();
    }
}
