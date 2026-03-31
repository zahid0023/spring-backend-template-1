package com.example.resortapplication1.model.mapper;

import com.example.resortapplication1.dto.request.templates.CreateTemplateRequest;
import com.example.resortapplication1.dto.request.templates.UpdateTemplateRequest;
import com.example.resortapplication1.model.dto.TemplateDto;
import com.example.resortapplication1.model.entity.TemplateEntity;
import lombok.experimental.UtilityClass;

import java.util.stream.Collectors;

@UtilityClass
public class TemplateMapper {

    public static TemplateEntity fromRequest(CreateTemplateRequest request) {
        TemplateEntity entity = new TemplateEntity();
        entity.setKey(request.getKey());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus() != null ? request.getStatus() : "draft");
        return entity;
    }

    public static void updateEntity(TemplateEntity entity, UpdateTemplateRequest request) {
        if (request.getKey() != null) entity.setKey(request.getKey());
        if (request.getName() != null) entity.setName(request.getName());
        if (request.getDescription() != null) entity.setDescription(request.getDescription());
        if (request.getStatus() != null) entity.setStatus(request.getStatus());
    }

    public static TemplateDto toDto(TemplateEntity entity) {
        return TemplateDto.builder()
                .id(entity.getId())
                .key(entity.getKey())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .templatePages(entity.getTemplatePageEntities().stream()
                        .map(TemplatePageMapper::toDto)
                        .collect(Collectors.toSet()))
                .build();
    }
}
