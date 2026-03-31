package com.example.resortapplication1.model.mapper;

import com.example.resortapplication1.dto.request.pagetypes.CreatePageTypeRequest;
import com.example.resortapplication1.dto.request.pagetypes.UpdatePageTypeRequest;
import com.example.resortapplication1.model.dto.PageTypeDto;
import com.example.resortapplication1.model.entity.PageTypeEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PageTypeMapper {

    public static PageTypeEntity fromRequest(CreatePageTypeRequest request) {
        PageTypeEntity entity = new PageTypeEntity();
        entity.setKey(request.getKey());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        return entity;
    }

    public static void updateEntity(PageTypeEntity entity, UpdatePageTypeRequest request) {
        if (request.getKey() != null) entity.setKey(request.getKey());
        if (request.getName() != null) entity.setName(request.getName());
        if (request.getDescription() != null) entity.setDescription(request.getDescription());
    }

    public static PageTypeDto toDto(PageTypeEntity entity) {
        return PageTypeDto.builder()
                .id(entity.getId())
                .key(entity.getKey())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }
}
