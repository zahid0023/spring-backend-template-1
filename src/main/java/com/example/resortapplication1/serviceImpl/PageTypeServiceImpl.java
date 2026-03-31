package com.example.resortapplication1.serviceImpl;

import com.example.resortapplication1.commons.dto.response.PaginatedResponse;
import com.example.resortapplication1.commons.dto.response.SuccessResponse;
import com.example.resortapplication1.dto.request.pagetypes.CreatePageTypeRequest;
import com.example.resortapplication1.dto.request.pagetypes.UpdatePageTypeRequest;
import com.example.resortapplication1.dto.response.pagetypes.PageTypeResponse;
import com.example.resortapplication1.model.dto.PageTypeDto;
import com.example.resortapplication1.model.entity.PageTypeEntity;
import com.example.resortapplication1.model.mapper.PageTypeMapper;
import com.example.resortapplication1.model.projection.PageTypeSummary;
import com.example.resortapplication1.repository.PageTypeRepository;
import com.example.resortapplication1.service.PageTypeService;
import com.example.resortapplication1.utils.Pagination;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Slf4j
public class PageTypeServiceImpl implements PageTypeService {

    private final PageTypeRepository pageTypeRepository;

    public PageTypeServiceImpl(PageTypeRepository pageTypeRepository) {
        this.pageTypeRepository = pageTypeRepository;
    }

    @Override
    public SuccessResponse createPageType(CreatePageTypeRequest request) {
        PageTypeEntity entity = PageTypeMapper.fromRequest(request);
        entity = pageTypeRepository.save(entity);
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public PageTypeResponse getPageType(Long id) {
        PageTypeEntity entity = getPageTypeById(id);
        return new PageTypeResponse(PageTypeMapper.toDto(entity));
    }

    @Override
    public PaginatedResponse<PageTypeDto> getAllPageTypes(Pageable pageable) {
        Page<@NonNull PageTypeSummary> page = pageTypeRepository
                .findAllByIsActiveAndIsDeleted(true, false, pageable, PageTypeSummary.class);

        Page<@NonNull PageTypeDto> dtoPage = page.map(p ->
                PageTypeDto.builder()
                        .id(p.getId())
                        .key(p.getKey())
                        .name(p.getName())
                        .build()
        );
        return Pagination.buildPaginatedResponse(dtoPage);
    }

    @Override
    public PageTypeResponse updatePageType(Long id, UpdatePageTypeRequest request) {
        PageTypeEntity entity = getPageTypeById(id);
        PageTypeMapper.updateEntity(entity, request);
        entity = pageTypeRepository.save(entity);
        return new PageTypeResponse(PageTypeMapper.toDto(entity));
    }

    @Override
    public SuccessResponse deletePageType(Long id) {
        PageTypeEntity entity = getPageTypeById(id);
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        pageTypeRepository.save(entity);
        return new SuccessResponse(true, id);
    }

    @Override
    public PageTypeEntity getPageTypeById(Long id) {
        return pageTypeRepository.findByIdAndIsActiveAndIsDeleted(id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("Page Type with id: " + id + " was not found."));
    }
}
