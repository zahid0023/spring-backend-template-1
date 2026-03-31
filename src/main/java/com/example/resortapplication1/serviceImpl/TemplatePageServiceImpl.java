package com.example.resortapplication1.serviceImpl;

import com.example.resortapplication1.commons.dto.response.PaginatedResponse;
import com.example.resortapplication1.commons.dto.response.SuccessResponse;
import com.example.resortapplication1.dto.request.templatepages.CreateTemplatePageRequest;
import com.example.resortapplication1.dto.request.templatepages.UpdateTemplatePageRequest;
import com.example.resortapplication1.dto.response.templatepages.TemplatePageResponse;
import com.example.resortapplication1.model.dto.TemplatePageDto;
import com.example.resortapplication1.model.entity.PageTypeEntity;
import com.example.resortapplication1.model.entity.TemplateEntity;
import com.example.resortapplication1.model.entity.TemplatePageEntity;
import com.example.resortapplication1.model.mapper.TemplatePageMapper;
import com.example.resortapplication1.repository.TemplatePageRepository;
import com.example.resortapplication1.service.PageTypeService;
import com.example.resortapplication1.service.TemplatePageService;
import com.example.resortapplication1.service.TemplateService;
import com.example.resortapplication1.utils.Pagination;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TemplatePageServiceImpl implements TemplatePageService {

    private final TemplatePageRepository templatePageRepository;
    private final TemplateService templateService;
    private final PageTypeService pageTypeService;

    public TemplatePageServiceImpl(TemplatePageRepository templatePageRepository,
                                   TemplateService templateService,
                                   PageTypeService pageTypeService) {
        this.templatePageRepository = templatePageRepository;
        this.templateService = templateService;
        this.pageTypeService = pageTypeService;
    }

    @Override
    public SuccessResponse createTemplatePage(CreateTemplatePageRequest request) {
        TemplateEntity template = templateService.getTemplateById(request.getTemplateId());
        PageTypeEntity pageType = pageTypeService.getPageTypeById(request.getPageTypeId());
        TemplatePageEntity entity = TemplatePageMapper.fromRequest(request, template, pageType);
        entity = templatePageRepository.save(entity);
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public TemplatePageResponse getTemplatePage(Long id) {
        TemplatePageEntity entity = getTemplatePageById(id);
        TemplatePageDto dto = TemplatePageMapper.toDto(entity);
        return new TemplatePageResponse(dto);
    }

    @Override
    public PaginatedResponse<TemplatePageDto> getAllTemplatePages(Pageable pageable) {
        Page<@NonNull TemplatePageEntity> page = templatePageRepository.findAllByIsActiveAndIsDeleted(true, false, pageable);
        Page<@NonNull TemplatePageDto> dtoPage = page.map(TemplatePageMapper::toDto);
        return Pagination.buildPaginatedResponse(dtoPage);
    }

    @Override
    public TemplatePageResponse updateTemplatePage(Long id, UpdateTemplatePageRequest request) {
        TemplatePageEntity entity = getTemplatePageById(id);

        TemplateEntity template = request.getTemplateId() != null
                ? templateService.getTemplateById(request.getTemplateId())
                : null;
        PageTypeEntity pageType = request.getPageTypeId() != null
                ? pageTypeService.getPageTypeById(request.getPageTypeId())
                : null;

        TemplatePageMapper.updateEntity(entity, request, template, pageType);
        entity = templatePageRepository.save(entity);
        TemplatePageDto dto = TemplatePageMapper.toDto(entity);
        return new TemplatePageResponse(dto);
    }

    @Override
    public SuccessResponse deleteTemplatePage(Long id) {
        TemplatePageEntity entity = getTemplatePageById(id);
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        templatePageRepository.save(entity);
        return new SuccessResponse(true, id);
    }

    @Override
    public TemplatePageEntity getTemplatePageById(Long id) {
        return templatePageRepository.findByIdAndIsActiveAndIsDeleted(id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("Template Page with id: " + id + " was not found."));
    }
}
