package com.example.resortapplication1.serviceImpl;

import com.example.resortapplication1.commons.dto.response.PaginatedResponse;
import com.example.resortapplication1.commons.dto.response.SuccessResponse;
import com.example.resortapplication1.dto.request.uiblockcategories.CreateUiBlockCategoryRequest;
import com.example.resortapplication1.dto.request.uiblockcategories.UpdateUiBlockCategoryRequest;
import com.example.resortapplication1.dto.response.uiblockcategories.UiBlockCategoryResponse;
import com.example.resortapplication1.model.dto.UiBlockCategoryDto;
import com.example.resortapplication1.model.entity.UiBlockCategoryEntity;
import com.example.resortapplication1.model.mapper.UiBlockCategoryMapper;
import com.example.resortapplication1.repository.UiBlockCategoryRepository;
import com.example.resortapplication1.service.UiBlockCategoryService;
import com.example.resortapplication1.utils.Pagination;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UiBlockCategoryServiceImpl implements UiBlockCategoryService {

    private final UiBlockCategoryRepository uiBlockCategoryRepository;

    public UiBlockCategoryServiceImpl(UiBlockCategoryRepository uiBlockCategoryRepository) {
        this.uiBlockCategoryRepository = uiBlockCategoryRepository;
    }

    @Override
    public SuccessResponse createUiBlockCategory(CreateUiBlockCategoryRequest request) {
        UiBlockCategoryEntity entity = UiBlockCategoryMapper.fromRequest(request);
        entity = uiBlockCategoryRepository.save(entity);
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public UiBlockCategoryResponse getUiBlockCategory(Long id) {
        UiBlockCategoryEntity entity = getUiBlockCategoryById(id);
        UiBlockCategoryDto dto = UiBlockCategoryMapper.toDto(entity);
        return new UiBlockCategoryResponse(dto);
    }

    @Override
    public PaginatedResponse<UiBlockCategoryDto> getAllUiBlockCategories(Pageable pageable) {
        Page<@NonNull UiBlockCategoryEntity> page = uiBlockCategoryRepository.findAllByIsActiveAndIsDeleted(true, false, pageable);
        Page<@NonNull UiBlockCategoryDto> dtoPage = page.map(UiBlockCategoryMapper::toDto);
        return Pagination.buildPaginatedResponse(dtoPage);
    }

    @Override
    public UiBlockCategoryResponse updateUiBlockCategory(Long id, UpdateUiBlockCategoryRequest request) {
        UiBlockCategoryEntity entity = getUiBlockCategoryById(id);
        UiBlockCategoryMapper.updateEntity(entity, request);
        entity = uiBlockCategoryRepository.save(entity);
        UiBlockCategoryDto dto = UiBlockCategoryMapper.toDto(entity);
        return new UiBlockCategoryResponse(dto);
    }

    @Override
    public SuccessResponse deleteUiBlockCategory(Long id) {
        UiBlockCategoryEntity entity = getUiBlockCategoryById(id);
        entity.setIsDeleted(true);
        entity.setIsActive(false);
        uiBlockCategoryRepository.save(entity);
        return new SuccessResponse(true, id);
    }

    @Override
    public UiBlockCategoryEntity getUiBlockCategoryById(Long id) {
        return uiBlockCategoryRepository.findByIdAndIsActiveAndIsDeleted(id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("UI Block Category with id: " + id + " was not found."));
    }
}
