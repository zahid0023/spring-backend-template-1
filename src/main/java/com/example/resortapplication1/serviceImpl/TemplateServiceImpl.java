package com.example.resortapplication1.serviceImpl;

import com.example.resortapplication1.commons.dto.response.PaginatedResponse;
import com.example.resortapplication1.commons.dto.response.SuccessResponse;
import com.example.resortapplication1.dto.request.templates.CreateTemplateRequest;
import com.example.resortapplication1.dto.request.templates.UpdateTemplateRequest;
import com.example.resortapplication1.dto.response.templates.TemplateResponse;
import com.example.resortapplication1.model.dto.TemplateDto;
import com.example.resortapplication1.model.entity.TemplateEntity;
import com.example.resortapplication1.model.mapper.TemplateMapper;
import com.example.resortapplication1.model.projection.TemplateSummary;
import com.example.resortapplication1.repository.TemplateRepository;
import com.example.resortapplication1.service.TemplateService;
import com.example.resortapplication1.utils.Pagination;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateServiceImpl(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public SuccessResponse createTemplate(CreateTemplateRequest request) {
        TemplateEntity entity = TemplateMapper.fromRequest(request);
        entity = templateRepository.save(entity);
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public TemplateResponse getTemplate(Long id) {
        TemplateEntity entity = getTemplateById(id);
        TemplateDto dto = TemplateMapper.toDto(entity);
        return new TemplateResponse(dto);
    }

    @Override
    public PaginatedResponse<TemplateDto> getAllTemplates(Pageable pageable) {
        Page<@NonNull TemplateSummary> page = templateRepository.findAllByIsActiveAndIsDeleted(true, false, pageable, TemplateSummary.class);
        Page<@NonNull TemplateDto> dtoPage = page.map(s -> TemplateDto.builder()
                .id(s.getId())
                .key(s.getKey())
                .name(s.getName())
                .status(s.getStatus())
                .build());
        return Pagination.buildPaginatedResponse(dtoPage);
    }

    @Override
    public TemplateResponse updateTemplate(Long id, UpdateTemplateRequest request) {
        TemplateEntity entity = getTemplateById(id);
        TemplateMapper.updateEntity(entity, request);
        entity = templateRepository.save(entity);
        TemplateDto dto = TemplateMapper.toDto(entity);
        return new TemplateResponse(dto);
    }

    @Override
    public SuccessResponse deleteTemplate(Long id) {
        TemplateEntity entity = getTemplateById(id);
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        templateRepository.save(entity);
        return new SuccessResponse(true, id);
    }

    @Override
    public TemplateEntity getTemplateById(Long id) {
        return templateRepository.findByIdAndIsActiveAndIsDeleted(id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("Template with id: " + id + " was not found."));
    }
}
