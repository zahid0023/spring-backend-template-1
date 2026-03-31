package com.example.resortapplication1.model.mapper;

import com.example.resortapplication1.dto.request.uiblockcategories.CreateUiBlockCategoryRequest;
import com.example.resortapplication1.dto.request.uiblockcategories.UpdateUiBlockCategoryRequest;
import com.example.resortapplication1.model.dto.UiBlockCategoryDto;
import com.example.resortapplication1.model.entity.UiBlockCategoryEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UiBlockCategoryMapper {

    public static UiBlockCategoryEntity fromRequest(CreateUiBlockCategoryRequest request) {
        UiBlockCategoryEntity entity = new UiBlockCategoryEntity();
        entity.setKey(request.getKey());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        return entity;
    }

    public static void updateEntity(UiBlockCategoryEntity entity, UpdateUiBlockCategoryRequest request) {
        if (request.getKey() != null) entity.setKey(request.getKey());
        if (request.getName() != null) entity.setName(request.getName());
        if (request.getDescription() != null) entity.setDescription(request.getDescription());
    }

    public static UiBlockCategoryDto toDto(UiBlockCategoryEntity entity) {
        return UiBlockCategoryDto.builder()
                .id(entity.getId())
                .key(entity.getKey())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }
}
